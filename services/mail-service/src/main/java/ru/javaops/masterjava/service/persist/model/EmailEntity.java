package ru.javaops.masterjava.service.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;
import ru.javaops.masterjava.persist.model.BaseEntity;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmailEntity extends BaseEntity {
    @NonNull
    @Column("to_email")
    private String to;
    @NonNull
    private String subject;
    @NonNull
    private String body;
    @NonNull
    @Column("send_date_time")
    private Timestamp sendDateTime;
}
