package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class City extends BaseEntity {
    @Column("full_name")
    private @NonNull
    String fullName;
    @Column("cityid")
    private @NonNull
    String cityId;

    public City(Integer id, String fullName, String cityId) {
        this(fullName, cityId);
        this.id = id;
    }
}
