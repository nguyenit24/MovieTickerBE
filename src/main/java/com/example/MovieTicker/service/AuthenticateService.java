package com.example.MovieTicker.service;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import com.example.MovieTicker.entity.InvalidatedToken;
import com.example.MovieTicker.entity.TaiKhoan;
import com.example.MovieTicker.entity.VaiTro;
import com.example.MovieTicker.exception.AppException;
import com.example.MovieTicker.exception.ErrorCode;
import com.example.MovieTicker.repository.InvalidatedRepository;
import com.example.MovieTicker.repository.TaiKhoanRepository;
import com.example.MovieTicker.request.AuthenticateRequest;
import com.example.MovieTicker.request.IntrospectRequest;
import com.example.MovieTicker.response.AuthenticateResponse;
import com.example.MovieTicker.response.IntrospectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Service
@FieldDefaults(level =  AccessLevel.PRIVATE)
@Slf4j
public class AuthenticateService {
    @Autowired
    TaiKhoanRepository taiKhoanRepository; // Sửa từ UserRepository

    @Autowired
    InvalidatedRepository invalidatedTokenRepository; // Sửa tên biến cho đúng chuẩn

    @Value("${jwt.Key}") // Lấy key từ application.yaml
    @NonFinal
    String singerKey;

    public AuthenticateResponse authenticated(AuthenticateRequest request){
        // Tìm kiếm theo TenDangNhap (là username và cũng là ID)
        var taiKhoan = taiKhoanRepository.findById(request.getUsername()).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND)
        );
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), taiKhoan.getMatKhau());

        if (!isAuthenticated) {
            throw new AppException(ErrorCode.UNTHENTICATED);
        }
        return
                AuthenticateResponse.builder()
                        .authenticated(true)
                        .token(generateToken(taiKhoan)) // Truyền vào đối tượng TaiKhoan
                        .build();
    }
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try{
            verifyToken(token); // Đổi tên hàm cho rõ nghĩa
        } catch (Exception e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    // Sửa tham số từ User sang TaiKhoan
    public String generateToken(TaiKhoan taiKhoan) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(taiKhoan.getTenDangNhap()) // Lấy tên đăng nhập
                .issuer("com.example.MovieTicker") // Sửa issuer cho phù hợp
                .expirationTime(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 giờ
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScopeString(taiKhoan)) // Gọi hàm đã sửa
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(singerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error signing JWT: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void logout(IntrospectRequest logoutRequest) throws JOSEException, ParseException {
        var token =  verifyToken(logoutRequest.getToken());

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(token.getJWTClaimsSet().getJWTID())
                .expiryDate(token.getJWTClaimsSet().getExpirationTime())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    // Đổi tên hàm từ signedJWT thành verifyToken cho rõ nghĩa hơn
    private SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(singerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if(!(verified && expirationTime.after(new Date()))){
            throw new AppException(ErrorCode.UNTHENTICATED);
        }

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNTHENTICATED);
        }

        return signedJWT;
    }

    // **Đây là phần quan trọng nhất: Sửa lại để phù hợp với mô hình 1 vai trò**
    public String buildScopeString(TaiKhoan taiKhoan) {
        StringJoiner scopeString = new StringJoiner(" ");
        VaiTro role = taiKhoan.getVaiTro();
        if (role != null) {
            // Thêm vai trò, ví dụ: ROLE_ADMIN
            scopeString.add("ROLE_" + role.getTenVaiTro().toUpperCase());

            // Thêm các quyền (permissions) của vai trò đó nếu có
            if (!CollectionUtils.isEmpty(role.getPermissions())){
                role.getPermissions().forEach(permission -> scopeString.add(permission.getName()));
            }
        }
        return scopeString.toString();
    }
}