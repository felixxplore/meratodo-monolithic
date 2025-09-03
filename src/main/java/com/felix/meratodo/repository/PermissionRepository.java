package com.felix.meratodo.repository;


import com.felix.meratodo.enums.PermissionType;
import com.felix.meratodo.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {

    boolean existsByUserIdAndResourceTypeAndResourceIdAndPermissionType(Long userId, String resourceType, Long resourceId, PermissionType permissionType);
}
