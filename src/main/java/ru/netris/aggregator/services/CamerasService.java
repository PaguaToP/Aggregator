package ru.netris.aggregator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.netris.aggregator.models.Camera;
import ru.netris.aggregator.models.SourceData;
import ru.netris.aggregator.models.TokenData;
import ru.netris.aggregator.util.CameraResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CamerasService {
    private final String testUrl;
    private final WebClient webClient;

    @Autowired
    public CamerasService(@Value("${testUrl}") String testUrl,
                          WebClient webClient) {
        this.testUrl = testUrl;
        this.webClient = webClient;
    }

    public List<Camera> getAllCameras() {
        return allCamerasList()
                .parallelStream()
                .map(this::applySourceData)
                .map(this::applyTokenData)
                .toList();
    }

    private List<Camera> allCamerasList() {
        Camera[] cameras = webClient.get()
                .uri(testUrl)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    throw new CameraResponseException();
                })
                .bodyToMono(Camera[].class)
                .block();

        return cameras == null
                ? new ArrayList<>()
                : Arrays.stream(cameras).toList();
    }

    private Camera applySourceData(Camera camera) {
        camera.setSourceData(
                webClient.get()
                        .uri(camera.getSourceDataUrl())
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> {
                            throw new CameraResponseException();
                        })
                        .bodyToMono(SourceData.class)
                        .block()
        );

        return camera;
    }

    private Camera applyTokenData(Camera camera) {
        camera.setTokenData(
                webClient.get()
                        .uri(camera.getTokenDataUrl())
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> {
                            throw new CameraResponseException();
                        })
                        .bodyToMono(TokenData.class)
                        .block()
        );

        return camera;
    }
}
