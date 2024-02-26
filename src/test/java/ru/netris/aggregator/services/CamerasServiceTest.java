package ru.netris.aggregator.services;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import ru.netris.aggregator.models.Camera;
import ru.netris.aggregator.util.CameraResponseException;

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CamerasServiceTest {

    private static MockWebServer mockBackEnd;
    private CamerasService camerasService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s/mainRequest",
                mockBackEnd.getPort());
        camerasService = new CamerasService(baseUrl, WebClient.create());
    }

    @Test
    public void getAllCameras() {
        Dispatcher mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("/mainRequest")) {
                    return new MockResponse()
                            .setBody(String.format(
                                    """
                                        [
                                            {
                                                "id": 3,
                                                "sourceDataUrl": "http://localhost:%s/source",
                                                "tokenDataUrl": "http://localhost:%s/token"
                                            }
                                        ]
                                    """
                                    , mockBackEnd.getPort(), mockBackEnd.getPort()))
                            .addHeader("Content-Type", "application/json");
                }
                if (request.getPath().contains("/source")) {
                    return new MockResponse()
                            .setBody("""
                                        {
                                            "urlType": "LIVE",
                                            "videoUrl": "rtsp://127.0.0.1/1"
                                        }
                                     """)
                            .addHeader("Content-Type", "application/json");
                }

                if (request.getPath().contains("/token")) {
                    return new MockResponse()
                            .setBody("""
                                        {
                                            "value": "fa4b5f64-249b-11e9-ab14-d663bd873d93",
                                            "ttl": 180
                                        }
                                     """)
                            .addHeader("Content-Type", "application/json");
                }

                return new MockResponse().setResponseCode(404);
            }
        };
        mockBackEnd.setDispatcher(mDispatcher);

        List<Camera> cameraList = camerasService.getAllCameras();

        Assertions.assertNotNull(cameraList);
        Assertions.assertEquals(cameraList.size(), 1);

        final Camera result = cameraList.get(0);
        Assertions.assertEquals(result.getId(), 3);
        Assertions.assertEquals(result.getSourceData().getUrlType(), "LIVE");
        Assertions.assertEquals(result.getSourceData().getVideoUrl(), "rtsp://127.0.0.1/1");
        Assertions.assertEquals(result.getTokenData().getValue(), "fa4b5f64-249b-11e9-ab14-d663bd873d93");
        Assertions.assertEquals(result.getTokenData().getTtl(), 180);
    }

    @Test
    public void getAllCameras_MainRequestFailed_ThrowException() {
        Dispatcher mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("/mainRequest")) {
                    return new MockResponse()
                            .setResponseCode(404);
                }

                return new MockResponse().setResponseCode(404);
            }
        };
        mockBackEnd.setDispatcher(mDispatcher);

        Assertions.assertThrows(
                CameraResponseException.class, () -> camerasService.getAllCameras());
    }

    @Test
    public void getAllCameras_EmptyResponse_ReturnEmptyList() {
        Dispatcher mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("/mainRequest")) {
                    return new MockResponse()
                            .setResponseCode(200);
                }

                return new MockResponse().setResponseCode(404);
            }
        };
        mockBackEnd.setDispatcher(mDispatcher);

        List<Camera> cameraList = camerasService.getAllCameras();

        Assertions.assertTrue(cameraList.isEmpty());
    }

    @Test
    public void getAllCameras_SourceRequestFailed_ThrowException() {
        Dispatcher mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("/mainRequest")) {
                    return new MockResponse()
                            .setBody(String.format(
                                    """
                                        [
                                            {
                                                "id": 3,
                                                "sourceDataUrl": "http://localhost:%s/source",
                                                "tokenDataUrl": "http://localhost:%s/token"
                                            }
                                        ]
                                    """
                                    , mockBackEnd.getPort(), mockBackEnd.getPort()))
                            .addHeader("Content-Type", "application/json");
                }
                if (request.getPath().contains("/source")) {
                    return new MockResponse()
                            .setResponseCode(404);
                }

                if (request.getPath().contains("/token")) {
                    return new MockResponse()
                            .setBody("""
                                        {
                                            "value": "fa4b5f64-249b-11e9-ab14-d663bd873d93",
                                            "ttl": 180
                                        }
                                     """)
                            .addHeader("Content-Type", "application/json");
                }

                return new MockResponse().setResponseCode(404);
            }
        };
        mockBackEnd.setDispatcher(mDispatcher);

        Assertions.assertThrows(
                CameraResponseException.class, () -> camerasService.getAllCameras());
    }

    @Test
    public void getAllCameras_TokenRequestFailed_ThrowException() {
        Dispatcher mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("/mainRequest")) {
                    return new MockResponse()
                            .setBody(String.format(
                                    """
                                        [
                                            {
                                                "id": 3,
                                                "sourceDataUrl": "http://localhost:%s/source",
                                                "tokenDataUrl": "http://localhost:%s/token"
                                            }
                                        ]
                                    """
                                    , mockBackEnd.getPort(), mockBackEnd.getPort()))
                            .addHeader("Content-Type", "application/json");
                }
                if (request.getPath().contains("/source")) {
                    return new MockResponse()
                            .setBody("""
                                        {
                                            "urlType": "LIVE",
                                            "videoUrl": "rtsp://127.0.0.1/1"
                                        }
                                     """)
                            .addHeader("Content-Type", "application/json");
                }

                if (request.getPath().contains("/token")) {
                    return new MockResponse()
                            .setResponseCode(404);
                }

                return new MockResponse().setResponseCode(404);
            }
        };
        mockBackEnd.setDispatcher(mDispatcher);

        Assertions.assertThrows(
                CameraResponseException.class, () -> camerasService.getAllCameras());
    }
}
