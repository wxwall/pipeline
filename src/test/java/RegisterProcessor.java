import java.util.Map;

import com.skynet.pipeline.AbstractPipelineProcessor;
import com.skynet.pipeline.PipelineException;

public class RegisterProcessor extends AbstractPipelineProcessor {


    @Override
    protected String process(final Map<String, Object> params)
            throws PipelineException {
        System.out.println("注册用户成功");
        return "Send_Email";
    }

}
