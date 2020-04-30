package source;

import java.io.IOException;

public class KsNormalizeClient implements KsNormalizerClientInt {
    @Override
    public String normalize(String KsId) throws IOException {
        if(KsId.endsWith("-Normalized")){
            return KsId;
        }
        return KsId.concat("-Normalized");
    }
}
