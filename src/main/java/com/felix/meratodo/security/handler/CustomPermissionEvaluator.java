package com.felix.meratodo.security.handler;


import com.felix.meratodo.enums.PermissionType;
import com.felix.meratodo.enums.TeamRole;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.PermissionRepository;
import com.felix.meratodo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {


    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private AuthService authService;



    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    @Cacheable(value = "permissions", key = "#authentication.principal.id+ '_' + #targetType+ '_' + #targetId+ '_' + #permission")
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        User user = (User) authentication.getPrincipal();
        Long resourceId = (Long) targetId;
        PermissionType permissionType;
        try{
           permissionType = PermissionType.valueOf(permission.toString().toUpperCase());
        }catch (IllegalArgumentException ex){
            return false;
        }

        String resourceType = targetType.toUpperCase();

        if(permissionRepository.existsByUserIdAndResourceTypeAndResourceIdAndPermissionType(user.getId(),resourceType,resourceId,permissionType)){
            return true;
        }

        if(authService.isAdmin(authentication)) {
            return true;
        }

        TeamRole requiredRoleForPermission = getRequiredRoleForPermission(permissionType);

        return switch (resourceType){
            case "PROJECT" -> authService.isProjectMember(resourceId,user.getId()) && authService.hasProjectRole(resourceId,user.getId(),requiredRoleForPermission);
            case "TEAM" -> authService.isTeamMember(resourceId,user.getId()) && authService.hasTeamRole(resourceId,user.getId(),requiredRoleForPermission);
            case "TASK" -> authService.canAccessTask(resourceId, user.getId(),permissionType.name().contains("UPDATE") || permissionType.name().contains("DELETE"));
            default -> false;
        };
    }


    private TeamRole getRequiredRoleForPermission(PermissionType permissionType){
        return switch (permissionType){
            case TEAM_READ, PROJECT_READ, TASK_READ -> TeamRole.VIEWER;
            case TEAM_UPDATE, PROJECT_UPDATE, TASK_UPDATE -> TeamRole.EDITOR;
            case TEAM_DELETE, PROJECT_DELETE, TASK_DELETE -> TeamRole.LEADER;
            default -> TeamRole.OWNER;
        };
    }
}
