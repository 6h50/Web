package com.levantrung.identity_service.service;

import com.levantrung.identity_service.dto.request.AuthenticationRequest;
import com.levantrung.identity_service.dto.request.IntrospectRequest;
import com.levantrung.identity_service.dto.response.AuthenticationResponse;
import com.levantrung.identity_service.dto.response.IntrospectResponse;
import com.levantrung.identity_service.entity.User;
import com.levantrung.identity_service.exception.AppException;
import com.levantrung.identity_service.exception.ErrorCode;
import com.levantrung.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {

    UserRepository userRepository;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expirationTime.after(new Date()))
                .build();
    }


    @NonFinal // khong exec vào constructor
    @Value("${jwt.signerKey}")
    protected String SIGNNER_KEY ;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Tìm user trong database bằng username, nếu không tồn tại thì ném ra lỗi với mã USER_NOT_EXITSED
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITSED));

        // Tạo một đối tượng mã hóa mật khẩu bằng thuật toán BCrypt với độ phức tạp là 10
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // So sánh mật khẩu người dùng nhập vào với mật khẩu đã mã hóa trong database
        // Trả về true nếu trùng khớp, ngược lại trả về false
        boolean authenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .authenticated(authenticated)
                .token(token)
                .build();
    }

    //Tạo token
    String generateToken(User user) {

        //Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); //thuật toán sử dụng

        //Payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("trunglee.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli() //hạn sử dụng
                ))
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        //Ký token, khóa ký và giải mã
        try {
            jwsObject.sign(new MACSigner(SIGNNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create JWT object", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRolesAsSet()))
            user.getRolesAsSet().forEach(stringJoiner::add);
        return stringJoiner.toString();
    }
}
