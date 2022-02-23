package com.example1.springh2test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
public class TestClass {
    DataBaseHandle databasehandle;

    public TestClass(DataBaseHandle databasehandle) {
        this.databasehandle = databasehandle;
    }

    @GetMapping("/api/table/create")
    public ResponseEntity<Boolean> test1(

    ) throws SQLException {
        databasehandle.createTable();
        return ResponseEntity.ok(true);
    }
//    public ResponseEntity<반환형 class명> test3(

            @PostMapping("api/table/files")
    public ResponseEntity<Boolean> test3(
            @RequestParam MultipartFile file) throws IOException {
        log.info(file.toString());
        log.info(file.getOriginalFilename());
        log.info(file.getResource().getFile().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        return ResponseEntity.status(HttpStatus.OK).body()
    }

    @PostMapping("api/table/insert")
    public ResponseEntity<Boolean> test2(
            @RequestParam String code,
            @RequestParam String name
    )
            throws SQLException {
        databasehandle.insertTuple(code, name);
        return ResponseEntity.ok(true);
    }

    @PostMapping("api/table/json")
    public ResponseEntity<Boolean> test4(
            @RequestBody TestJson json
    )
    {
        log.info(json.toString());
        return ResponseEntity.ok(true);
    }


}
