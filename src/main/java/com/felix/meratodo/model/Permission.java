package com.felix.meratodo.model;

import com.felix.meratodo.enums.PermissionType;
import com.felix.meratodo.enums.ResourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private Long resourceId;

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;
}
