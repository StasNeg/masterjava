package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;
import ru.javaops.masterjava.persist.model.type.UserFlag;


import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends BaseEntity {
    @Column("full_name")
    private @NonNull String fullName;
    private @NonNull String email;
    private @NonNull UserFlag flag;
    @Column("city_ref")
    private @NonNull String cityRef;
    //    ManyToMAny user_Group
    private Set<Integer> groupsId;

    public User(String fullName, String email, UserFlag flag, String cityRef) {
        this(fullName, email, flag, cityRef, null);
    }
    public User(Integer id, String fullName, String email, UserFlag flag, String cityRef) {
        this(fullName, email, flag, cityRef, null);
        this.id=id;
    }

    public User(Integer id, String fullName, String email, UserFlag flag, String cityRef, Set<Integer> groupId) {
        this(fullName, email, flag, cityRef, groupId);
        this.id = id;
    }
}