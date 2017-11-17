package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Group extends BaseEntity {

    private @NonNull
    String name;
    private @NonNull
    GroupType type;
    private @NonNull
    Integer projectId;

    public Group(Integer id, String name, GroupType type, Integer projectId) {
        this(name, type, projectId);
        this.id = id;
    }
}
