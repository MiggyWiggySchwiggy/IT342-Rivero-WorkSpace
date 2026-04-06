package edu.cit.rivero.workspace.controller;

import edu.cit.rivero.workspace.common.ApiResponse;
import edu.cit.rivero.workspace.entity.Space;
import edu.cit.rivero.workspace.repository.SpaceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/spaces")
@CrossOrigin(origins = "*")
public class SpaceController {

    private final SpaceRepository spaceRepository;

    public SpaceController(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Space>>> getAllSpaces() {
        return ResponseEntity.ok(ApiResponse.success(spaceRepository.findAll()));
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<Space>> getSpaceById(@PathVariable String spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found."));
        return ResponseEntity.ok(ApiResponse.success(space));
    }
}
