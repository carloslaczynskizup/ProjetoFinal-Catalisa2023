package com.squad3.bemestar.controller;

import com.squad3.bemestar.domain.dto.CampanhasDTO;
import com.squad3.bemestar.domain.entity.Campanhas;
import com.squad3.bemestar.service.CampanhasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/campanhas")
public class CampanhasController {

    @Autowired
    private CampanhasService campanhasService;

    //Endpoint para listar todas as campanhas
    @GetMapping
    public ResponseEntity<List<CampanhasDTO>> listarCampanhas() {
        try {
            List<Campanhas> campanhas = campanhasService.listarCampanhas();
            List<CampanhasDTO> campanhasDTOS = campanhas.stream()
                    .map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(campanhasDTOS);
        } catch (CampanhaNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Endpoint para listar campanhas por id
    @GetMapping("/{id}")
    public ResponseEntity<CampanhasDTO> listarCampanhasPorId(@PathVariable Long id) {
        try {
            Campanhas campanhas = campanhasService.listarCampanhasPorId(id);
            if (campanhas == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                CampanhasDTO campanhasDTO = convertToDTO(campanhas);
                return ResponseEntity.ok(campanhasDTO);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }

    //Endpoint para criar uma nova campanha
    @PostMapping
    public ResponseEntity<CampanhasDTO> criarCampanha(@RequestBody CampanhasDTO campanhasDTO) {
        try {
            Campanhas campanhas = convertToEntity(campanhasDTO);
            Campanhas campanhasCriada = campanhasService.criarCampanha(campanhas);
            CampanhasDTO campanhasCriadaDTO = convertToDTO(campanhasCriada);
            return ResponseEntity.status(HttpStatus.CREATED).body(campanhasCriadaDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Endpoint para atualizar uma campanha
    @PutMapping("/{id}")
    public ResponseEntity<CampanhasDTO> atualizarCampanha(@PathVariable Long id,
                                                          @RequestBody CampanhasDTO campanhasDTO) {
        try {
            Campanhas campanhas = convertToEntity(campanhasDTO);
            Campanhas campanhasAtualizada = campanhasService.atualizarCampanha(id, campanhas);
            if (campanhasAtualizada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                CampanhasDTO campanhasAtualizadaDTO = convertToDTO(campanhasAtualizada);
                return ResponseEntity.ok(campanhasAtualizadaDTO);
            }

        } catch (CampanhaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Endpoint para deletar uma campanha
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCampanha(@PathVariable Long id) {
        try {
            boolean deletado = campanhasService.deletarCampanha(id);
            if (deletado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Métodos de conversão (convertToDTO e convertToEntity) para converter entre a entidade e o DTO.

    private CampanhasDTO convertToDTO(Campanhas campanhas) {
        CampanhasDTO campanhasDTO = new CampanhasDTO();
        campanhasDTO.setId(campanhas.getId());
        campanhasDTO.setNomeCampanha(campanhas.getNomeCampanha());
        campanhasDTO.setDataInicio(campanhas.getDataInicio());
        campanhasDTO.setDataFim(campanhas.getDataFim());
        return campanhasDTO;
    }

    private Campanhas convertToEntity(CampanhasDTO campanhasDTO) {
        Campanhas campanhas = new Campanhas();
        campanhas.setId(campanhasDTO.getId());
        campanhas.setNomeCampanha(campanhasDTO.getNomeCampanha());
        campanhas.setDataInicio(campanhasDTO.getDataInicio());
        campanhas.setDataFim(campanhasDTO.getDataFim());
        return campanhas;
    }


    public static class CampanhaNotFoundException extends RuntimeException {
        public CampanhaNotFoundException(Long id) {
            super("Campanha com ID " + id + " não encontrada.");
        }
    }
}
