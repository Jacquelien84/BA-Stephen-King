package nl.oudhoff.bastephenking.exception;

public class TokenGenerationFailedException extends RuntimeException {
  public TokenGenerationFailedException(String message) {
    super(message);
  }
}
