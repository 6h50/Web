package com.levantrung.identity_service.configuration;

import com.levantrung.identity_service.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Khai báo các endpoint công khai (không yêu cầu xác thực)
    private final String[] PUBLC_ENDPOINTS = {
            "/users",                 // Đăng ký hoặc lấy thông tin người dùng
            "/auth/introspect",      // Kiểm tra token hợp lệ
            "/auth/token"             // Cấp token

    };

    // Inject giá trị từ file application.properties với key là "jwt.signerKey"
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Cấu hình quyền truy cập cho các request
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(requests ->
                        requests
                                // Cho phép tất cả người dùng gửi request POST tới các endpoint công khai (không cần xác thực)
                                .requestMatchers(HttpMethod.POST, PUBLC_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name()) //chir có admin mới get ALl users
                                // Tất cả các request khác đều yêu cầu xác thực
                                .anyRequest().authenticated()
                )
                // Tắt tính năng bảo vệ CSRF vì đây là API REST (không sử dụng session hoặc form)
                .csrf(AbstractHttpConfigurer::disable)
                // Cấu hình xác thực bằng JWT (OAuth2 Resource Server)
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer ->
                                // Sử dụng jwtDecoder() để kiểm tra và giải mã JWT token
                                jwtConfigurer.decoder(jwtDecoder())
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        // Trả về đối tượng SecurityFilterChain đã cấu hình
        return httpSecurity.build();
    }

    //Chuyển SCOPE thành ROLE
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        // Tạo một đối tượng SecretKeySpec từ khóa bí mật (secret key) và thuật toán sử dụng.
        // - signerKey.getBytes(): chuyển chuỗi signerKey thành mảng byte.
        // - "HS512": thuật toán HMAC SHA-512 sẽ được sử dụng để giải mã JWT.
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");

        // Sử dụng NimbusJwtDecoder (thư viện của Spring Security) để xây dựng đối tượng JwtDecoder.
        // Cấu hình giải mã JWT bằng secret key và thuật toán HMAC SHA-512.
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)          // Thiết lập khóa bí mật
                .macAlgorithm(MacAlgorithm.HS512)      // Chỉ định thuật toán HMAC để xác thực token
                .build();                               // Xây dựng đối tượng JwtDecoder
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}