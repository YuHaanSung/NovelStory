package com.example.demo.service;

import com.example.demo.repository.DataStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
public class FileService {
    private final DataStore dataStore;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String FILE_PATH = "novel_data.json";

    public FileService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // 서버 시작 시 자동 실행: 파일 -> 리스트
    @PostConstruct
    public void loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                DataStore loaded = mapper.readValue(file, DataStore.class);
                dataStore.authorList = loaded.authorList;
                dataStore.novelList = loaded.novelList;
                dataStore.novelPartList = loaded.novelPartList;
                dataStore.commentList = loaded.commentList;
                dataStore.commentIdCounter = loaded.commentIdCounter;
                System.out.println("✅ 기존 데이터를 성공적으로 불러왔습니다.");
            } catch (IOException e) {
                System.err.println("❌ 데이터 로딩 중 오류 발생: " + e.getMessage());
            }
        }
    }

    // 데이터 변경 시 호출: 리스트 -> 파일
    public void saveData() {
        try {
            mapper.writeValue(new File(FILE_PATH), dataStore);
            System.out.println("💾 데이터가 파일에 저장되었습니다.");
        } catch (IOException e) {
            System.err.println("❌ 데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }
}