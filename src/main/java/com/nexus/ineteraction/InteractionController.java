package com.nexus.ineteraction;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("interactions")
public class InteractionController {

    private final InteractionRepository interactionRepository;
    private final InteractionMapper interactionMapper;

    public InteractionController(InteractionRepository interactionRepository, InteractionMapper interactionMapper) {
        this.interactionRepository = interactionRepository;
        this.interactionMapper = interactionMapper;
    }

    @GetMapping
    public List<ListInteractionResponse> getAll() {
        return interactionRepository.findAll().stream().map(interactionMapper::toListInteractionResponse).toList();
    }

    @GetMapping("{id}")
    public InteractionResponse getById(@PathVariable Integer id) {
        Interaction interaction = interactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Interaction not found")
        );

        return interactionMapper.toInteractionResponse(interaction);
    }
}
