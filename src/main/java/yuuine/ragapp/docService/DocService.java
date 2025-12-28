package yuuine.ragapp.docService;

import yuuine.ragapp.dto.response.DocList;

public interface DocService {

    void saveDoc(String fileMd5, String fileName);

    DocList getDoc();
}
