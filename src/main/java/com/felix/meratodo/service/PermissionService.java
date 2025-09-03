package com.felix.meratodo.service;

import com.felix.meratodo.enums.PermissionType;
import com.felix.meratodo.enums.ResourceType;
import com.felix.meratodo.model.Permission;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;


    public void grantPermission(User user, String resourceType, Long resourceId, PermissionType permissionType){
        Permission permission=new Permission();
        permission.setUser(user);
        permission.setResourceType(ResourceType.valueOf(resourceType.toUpperCase()));
        permission.setResourceId(resourceId);
        permission.setPermissionType(permissionType);

        permissionRepository.save(permission);
    }
}
