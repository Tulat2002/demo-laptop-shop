package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDto;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        //check exits
        if (this.permissionService.isPermissionExist(permission)){
            throw new IdInvalidException("Permission da ton tai");
        }
        //create permission
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.createPermission(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        //check exist by id;
        if (this.permissionService.fetchById(permission.getId()) == null){
            throw new IdInvalidException("Permission voi id " + permission.getId() + " khong ton tai");
        }
        //check exist by module and apiPath and method
        if (this.permissionService.isPermissionExist(permission)){
            //check name
            if (this.permissionService.isSameName(permission)){
                throw new IdInvalidException("Permission da ton tai");
            }
        }
        //update permission
        return ResponseEntity.ok().body(this.permissionService.updatePermission(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        //check id
        if (this.permissionService.fetchById(id) == null){
            throw new IdInvalidException("Permission voi id " + id + " khong ton tai");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permission paginate")
    public ResponseEntity<ResultPaginationDto> fetchAllPermissions(
            @Filter Specification<Permission> specification, Pageable pageable){
        return ResponseEntity.ok().body(this.permissionService.fetchAllPermissions(specification, pageable));
    }

}
