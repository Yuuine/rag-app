package yuuine.ragapp.docService.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuuine.ragapp.docService.DocService;
import yuuine.ragapp.docService.entity.RagDocuments;
import yuuine.ragapp.docService.repository.DocMapper;
import yuuine.ragapp.dto.response.DocList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DocServiceImpl implements DocService {

    private final DocMapper docMapper;

    @Override
    public void saveDoc(String fileMd5, String fileName) {

        docMapper.saveDoc(fileMd5, fileName, LocalDateTime.now());

    }

    @Override
    public DocList getDoc() {

        List<RagDocuments> docs = docMapper.getDoc();

        DocList docList = new DocList();
        if (docs != null) {
            docList.setDocs(docs);
            log.info("获取文档列表成功，共计 {} 份", docs.size());
        } else {
            docList.setDocs(new ArrayList<>());
            log.info("文档列表为空");
        }

        return docList;
    }

}
