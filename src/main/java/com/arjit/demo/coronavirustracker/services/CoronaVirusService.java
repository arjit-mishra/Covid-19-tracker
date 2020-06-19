package com.arjit.demo.coronavirustracker.services;

import com.arjit.demo.coronavirustracker.constants.Url;
import com.arjit.demo.coronavirustracker.models.LocationStats;
import com.arjit.demo.coronavirustracker.models.RecoveryStats;
import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusService {

    @Getter
    private List<LocationStats> allStats = new ArrayList<>();

    @Getter
    List<RecoveryStats> allRecoveryStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "* * 1 * * * ")
    public void getTotalRecovery() throws IOException, InterruptedException {

        List<RecoveryStats> recoveryStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestRecoveredCases = HttpRequest.newBuilder()
                .uri(URI.create(Url.VIRUS_CASES_RECOVERED_DATA_URL))
                .build();

        //Recovered
        HttpResponse<String> httpResponseRecovered = client.send(requestRecoveredCases, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReaderRecovered = new StringReader(httpResponseRecovered.body());
        Iterable<CSVRecord> recoveredRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderRecovered);

        for (CSVRecord record : recoveredRecords){
            RecoveryStats recoveryStat = new RecoveryStats();
            recoveryStat.setLatestTotalRecovery(Long.parseLong(record.get(record.size() - 1)));
            recoveryStat.setRecoveryDifferenceFromPreviousDay(Long.parseLong(record.get(record.size() - 2)));
            recoveryStats.add(recoveryStat);
        }
        this.allRecoveryStats=recoveryStats;

    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * * ")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        List<Long> death = new ArrayList<>();
        List<Long> deathPreviousDay = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestCurrentCases = HttpRequest.newBuilder()
                .uri(URI.create(Url.VIRUS_CASES_CONFIRMED_DATA_URL))
                .build();

        HttpRequest requestDeathCases = HttpRequest.newBuilder()
                .uri(URI.create(Url.VIRUS_CASES_DEATH_DATA_URL))
                .build();

        //get data, parse and save

        //confirmed
        HttpResponse<String> httpResponseConfirmed = client.send(requestCurrentCases, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReaderConfirmed = new StringReader(httpResponseConfirmed.body());
        Iterable<CSVRecord> confirmedRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderConfirmed);

        //Death
        HttpResponse<String> httpResponseDeath = client.send(requestDeathCases, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReaderDeath = new StringReader(httpResponseDeath.body());
        Iterable<CSVRecord> deathRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderDeath);


        for (CSVRecord record : confirmedRecords) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            long latestCases = Long.parseLong(record.get(record.size() - 1));
            long previousCases = Long.parseLong(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDifferenceFromPreviousDay(latestCases-previousCases);
//            System.out.println(locationStat);
            newStats.add(locationStat);
        }

        this.allStats=newStats;

        for (CSVRecord record : deathRecords){
            death.add(Long.parseLong(record.get(record.size() - 1)));
            deathPreviousDay.add(Long.parseLong(record.get(record.size() - 2)));
        }
//
        for (int i=0;i<allStats.size();i++){
//            allStats.get(i).setLatestTotalRecovery(recovered.get(i));
            allStats.get(i).setLatestTotalDeath(death.get(i));
//            allStats.get(i).setRecoveryDifferenceFromPreviousDay(recoveredPreviousDay.get(i));
            allStats.get(i).setDeathDifferenceFromPreviousDay(deathPreviousDay.get(i));
        }


    }

}

