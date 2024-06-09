package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDto;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission){
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );
    }

    public Permission createPermission(Permission permission){
        return this.permissionRepository.save(permission);
    }

    public Permission fetchById(long id){
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()){
            return permissionOptional.get();
        }
        return null;
    }

    public Permission updatePermission(Permission permission){
        Permission permissionDb = this.fetchById(permission.getId());
        if (permissionDb != null){
            permissionDb.setName(permission.getName());
            permissionDb.setApiPath(permission.getApiPath());
            permissionDb.setMethod(permission.getMethod());
            permissionDb.setModule(permission.getModule());
            //update permission
            permissionDb = this.permissionRepository.save(permissionDb);
            return permissionDb;
        }
        return null;
    }

    public void deletePermission(long id){
        Optional<Permission> optionalPermission = this.permissionRepository.findById(id);
        Permission currentPermission = optionalPermission.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
        //deletePermission
        this.permissionRepository.delete(currentPermission);
    }

    public ResultPaginationDto fetchAllPermissions(Specification<Permission> specification, Pageable pageable){
        Page<Permission> permissionPage = this.permissionRepository.findAll(specification, pageable);
        ResultPaginationDto rs = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() +1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(permissionPage.getTotalPages());
        meta.setTotal(permissionPage.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(permissionPage.getContent());
        return rs;
    }

    public boolean isSameName(Permission permission){
        Permission permissionDb = this.fetchById(permission.getId());
        if (permissionDb != null){
            if (permissionDb.getName().equals(permission.getName()))
                return true;
        }
        return false;
    }

}
