package com.embabel.template.endpoint;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RambleEndpoint {

    @GetMapping("/ramble")
    public String ramble(@RequestBody String body) {
      System.out.println(body);
      return "Hello, world!";
    }

  @CrossOrigin
  @PostMapping(value = "/ramble", consumes = "text/plain", produces = "application/json")
  public Map<String, String> processText(@RequestBody String body) {
      System.out.println("Post: " + body);
      // Trim and create a short preview
      String preview = body.length() > 50 ? body.substring(0, 50) + "..." : body;
      return Map.of(
        "status", "received",
        "length", String.valueOf(body.length()),
        "preview", preview
      );
  }
}
