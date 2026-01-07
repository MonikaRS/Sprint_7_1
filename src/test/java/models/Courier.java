package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Courier {
    private final String login;
    private final String password;
    private final String firstName;

    @Override
    public String toString() {
        return "Courier{" +
                "login='" + login + '\'' +
                ", password='[PROTECTED]'" +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}