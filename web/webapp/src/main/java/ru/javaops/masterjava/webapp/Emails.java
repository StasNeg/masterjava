package ru.javaops.masterjava.webapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emails {
    private boolean[] isSend;
}
