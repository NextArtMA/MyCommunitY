package com.nextArt.community.service;

import com.nextArt.community.DTO.NotificationDTO;
import com.nextArt.community.DTO.PaginationDTO;
import com.nextArt.community.enums.NotificationStatusEnums;
import com.nextArt.community.enums.NotificationTypeEnums;
import com.nextArt.community.exception.CustomizeErrorCode;
import com.nextArt.community.exception.CustomizeException;
import com.nextArt.community.mapper.NotificationMapper;
import com.nextArt.community.mapper.UserMapper;
import com.nextArt.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;


    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        Integer totalPage;


        Integer totalCount = (int) notificationMapper.countByExample(new NotificationExample());
        if (totalCount % size ==0) {
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size +1;
        }

        if (page<1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage,page);

        //分页公式，每一页展示5条数据
        Integer offSet = size*(page-1);

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample,new RowBounds(offSet,size));
        if (notifications.size() == 0){
            return paginationDTO;
        }
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnums.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnums.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    @Transactional
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null ){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(),user.getId()) ){
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnums.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnums.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
