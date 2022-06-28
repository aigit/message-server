package com.qlk.message.server.service.impl;

import org.springframework.stereotype.Service;

import com.qlk.baymax.common.exception.BusinessException;
import com.qlk.message.server.bean.PushParams;
import com.qlk.message.server.service.IUserPushService;
import com.qlk.message.server.vo.GroupMessageVO;

/**
 * @author Ldl
 * @date 2018/11/13 16:00
 * @since 1.0.0
 * 患者的push暂无需求场景 暂不实现
 */
@Service("patientService")
public class PatientPushServiceImpl implements IUserPushService {
    @Override
    public void saveTaskPush(String taskId) throws BusinessException {

    }

    @Override
    public void sendSinglePushMessage(PushParams pushParams) throws BusinessException {

    }

    @Override
    public void pushToAllUser(String message, String linkUrl) throws BusinessException {

    }

    @Override
    public void batchPushByGroup(String message, String linkUrl, String recipientCacheTopushKey) throws BusinessException {

    }

    @Override
    public void sendBatchMessage(String pushBatchId) throws BusinessException {

    }

    @Override
    public void reSendPushByGroup() {

    }

    @Override
    public GroupMessageVO getGroupMessageInCache() {
        return null;
    }
}
