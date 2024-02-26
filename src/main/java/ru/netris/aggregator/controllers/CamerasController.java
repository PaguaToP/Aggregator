package ru.netris.aggregator.controllers;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netris.aggregator.dto.CameraDTO;
import ru.netris.aggregator.models.Camera;
import ru.netris.aggregator.services.CamerasService;
import ru.netris.aggregator.util.CameraResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cameras")
public class CamerasController {

    private final CamerasService camerasService;
    private final ModelMapper modelMapper;

    @Autowired
    public CamerasController(CamerasService camerasService,
                             ModelMapper modelMapper) {
        this.camerasService = camerasService;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    private void init() {
        // Setup mapping Camera -> CameraDTO
        TypeMap<Camera, CameraDTO> propertyMapper = modelMapper.createTypeMap(Camera.class, CameraDTO.class);
        propertyMapper.addMappings(
                mapper -> {
                    mapper.map(src -> src.getSourceData().getVideoUrl(), CameraDTO::setVideoUrl);
                    mapper.map(src -> src.getSourceData().getUrlType(), CameraDTO::setUrlType);
                    mapper.map(src -> src.getTokenData().getTtl(), CameraDTO::setTtl);
                    mapper.map(src -> src.getTokenData().getValue(), CameraDTO::setValue);
                }
        );
    }

    @GetMapping()
    public List<CameraDTO> getAllCameras() {
        return convertToCameraDTO(camerasService.getAllCameras());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleException(CameraResponseException e) {
        Map<String, String> map = new HashMap<>();
        map.put("message", "Не удалось получить камеры.");

        return new ResponseEntity<>(map, HttpStatus.SERVICE_UNAVAILABLE);
    }

    private List<CameraDTO> convertToCameraDTO(List<Camera> cameraList) {
        return cameraList.stream()
                .map(camera -> modelMapper.map(camera, CameraDTO.class)).toList();
    }
}
