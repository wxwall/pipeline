import java.util.Map;

import com.skynet.pipeline.AbstractPipelineProcessor;
import com.skynet.pipeline.PipelineEngine;
import com.skynet.pipeline.PipelineException;

public class SendEmailProcessor extends AbstractPipelineProcessor {

    @Override
    protected String process(final Map<String, Object> params)
            throws PipelineException {
        System.out.println("发邮件给用户");
        return PipelineEngine.STOP_AND_COMMIT;
    }

}
