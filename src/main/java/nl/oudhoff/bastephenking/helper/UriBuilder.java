package nl.oudhoff.bastephenking.helper;

import nl.oudhoff.bastephenking.interfaces.IdentifiableUsername;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class UriBuilder {
    public static URI buildUriWithUsername(IdentifiableUsername uriObject) {
        return URI.create((
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + uriObject.getUsername()).toUriString())
        );
    }
}

