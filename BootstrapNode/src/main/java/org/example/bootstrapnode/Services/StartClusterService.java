package org.example.bootstrapnode.Services;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class StartClusterService {


    DistributionService distributionService = new DistributionService();
    public void startCluster() throws IOException {

        distributionService.init();

    }
}
