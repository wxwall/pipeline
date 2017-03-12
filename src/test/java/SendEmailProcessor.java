import java.util.Map;

import com.dmall.pipeline.AbstractPipelineProcessor;
import com.dmall.pipeline.PipelineEngine;
import com.dmall.pipeline.PipelineException;

public class SendEmailProcessor extends AbstractPipelineProcessor {

    @Override
    protected int process(final Map<String, Object> params)
            throws PipelineException {
        System.out.println("发邮件给用户");
        return PipelineEngine.STOP_AND_COMMIT;
    }

}
