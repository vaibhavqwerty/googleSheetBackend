package com.example.demo.service;

import com.example.demo.configuration.GoogleAuthorizationConfig;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleSheetsService {


    private String spreadsheetId = "1OvEBUMOlzJHUdOhp97b56H2w0ByYWzby3-CoEpSjwwY";

    @Autowired
    private GoogleAuthorizationConfig googleAuthorizationConfig;


    public void getSpreadsheetValues() throws GeneralSecurityException, IOException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();


        final String spreadsheetId = "1OvEBUMOlzJHUdOhp97b56H2w0ByYWzby3-CoEpSjwwY";
        final String range = "Class Data!A2:B";
        Sheets service = googleAuthorizationConfig.getSheetsService();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
//            System.out.println("Name, Major");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row.get(0), row.get(1));
            }
        }

//        Sheets.Spreadsheets.Values.BatchGet request =
//                sheetsService.spreadsheets().values().batchGet(spreadsheetId);
//        request.setRanges(getSpreadSheetRange());
//        request.setMajorDimension("ROWS");
//        BatchGetValuesResponse response = request.execute();
//        List<List<Object>> spreadSheetValues = response.getValueRanges().get(0).getValues();
//        List<Object> headers = spreadSheetValues.remove(0);
//        for (List<Object> row : spreadSheetValues) {
////            LOGGER.info("{}: {}, {}: {}, {}: {}, {}: {}",
////                    headers.get(0),row.get(0), headers.get(1),row.get(1),
////                    headers.get(2),row.get(2), headers.get(3),row.get(3));
//            System.out.println(headers.get(0));
//        }

    }

    public void addRow(String username, String number) throws GeneralSecurityException, IOException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();

        final String spreadsheetId = "1OvEBUMOlzJHUdOhp97b56H2w0ByYWzby3-CoEpSjwwY";
        final String range = "Class Data!A2:B";
        // Create new row data
        List<Object> rowData = new ArrayList<>();
        rowData.add(username);
        rowData.add(number);
        // Prepare the update request
        ValueRange body = new ValueRange().setValues(Arrays.asList(rowData));
        AppendValuesResponse result = sheetsService.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW") // You can use "USER_ENTERED" for formatted input
                .execute();

        System.out.printf("%d cells appended.\n", result.getUpdates().getUpdatedCells());
    }

}