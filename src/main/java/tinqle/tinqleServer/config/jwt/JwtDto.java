package tinqle.tinqleServer.config.jwt;

public record JwtDto (
    String accessToken,
    String refreshToken
) {}
