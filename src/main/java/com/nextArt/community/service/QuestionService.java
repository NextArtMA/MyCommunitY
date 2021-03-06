package com.nextArt.community.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.nextArt.community.exception.CustomizeErrorCode;
import com.nextArt.community.exception.CustomizeException;
import com.nextArt.community.mapper.QuestionExtMapper;
import com.nextArt.community.model.QuestionExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextArt.community.DTO.PaginationDTO;
import com.nextArt.community.DTO.QuestionDTO;
import com.nextArt.community.mapper.QuestionMapper;
import com.nextArt.community.mapper.UserMapper;
import com.nextArt.community.model.Question;
import com.nextArt.community.model.User;

@Service
public class QuestionService {

	@Autowired
	private QuestionMapper questionMapper;
	
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private QuestionExtMapper questionExtMapper;

	public PaginationDTO list(Integer page, Integer size) {
		PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
		Integer totalPage;
		Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
		if (totalCount % size ==0) {
			totalPage = totalCount / size;
		}else {
			totalPage = totalCount / size +1;
		}
		
		paginationDTO.setPagination(totalPage,page);
		
		if (page<1) {
			page = 1;
		}
		if (page > totalPage) {
			page = totalPage;
		}
		//分页公式，每一页展示5条数据
		Integer offSet = size*(page-1);
		QuestionExample questionExample = new QuestionExample();
		//倒序
		questionExample.setOrderByClause("gmt_create desc");
		List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample,new RowBounds(offSet,size));
		List<QuestionDTO> questionDTOList = new ArrayList<QuestionDTO>();
		
		for (Question question:questions) {
			User user = userMapper.selectByPrimaryKey(question.getCreator());
			QuestionDTO questionDTO = new QuestionDTO();
			BeanUtils.copyProperties(question, questionDTO);
			questionDTO.setUser(user);
			questionDTOList.add(questionDTO);
		}
		
		paginationDTO.setData(questionDTOList);
		return paginationDTO;
	}

	public PaginationDTO list(Long userId, Integer page, Integer size) {
		PaginationDTO paginationDTO = new PaginationDTO();
		
		Integer totalPage;
		QuestionExample questionExample = new QuestionExample();
		questionExample.createCriteria().andCreatorEqualTo(userId);

		Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
		if (totalCount % size ==0) {
			totalPage = totalCount / size;
		}else {
			totalPage = totalCount / size +1;
		}
		
		paginationDTO.setPagination(totalPage,page);
		
		if (page<1) {
			page = 1;
		}
		if (page > totalPage) {
			page = totalPage;
		}
		
		//分页公式，每一页展示5条数据
		Integer offSet = size*(page-1);
		QuestionExample example = new QuestionExample();
		example.createCriteria().andCreatorEqualTo(userId);
		List<Question> questions = questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offSet,size));
		List<QuestionDTO> questionDTOList = new ArrayList<QuestionDTO>();
		
		for (Question question:questions) {
			User user = userMapper.selectByPrimaryKey(question.getCreator());
			QuestionDTO questionDTO = new QuestionDTO();
			BeanUtils.copyProperties(question, questionDTO);
			questionDTO.setUser(user);
			questionDTOList.add(questionDTO);
		}
		
		paginationDTO.setData(questionDTOList);
		return paginationDTO;
	}

	public QuestionDTO getById(Long id) {
		Question question = questionMapper.selectByPrimaryKey(id);
		if (question == null){
			throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
		}
		QuestionDTO questionDTO = new QuestionDTO();
		BeanUtils.copyProperties(question, questionDTO);
		User user = userMapper.selectByPrimaryKey(questionDTO.getCreator());
		questionDTO.setUser(user);
		return questionDTO;
	}

    public void createOrUpdate(Question question) {
		if (question.getId() == null){
			//创建
			question.setGmtCreate(System.currentTimeMillis());
			question.setGmtModified(question.getGmtCreate());
			question.setViewCount(0);
			question.setLikeCount(0);
			question.setCommentCount(0);
			questionMapper.insertSelective(question);
		}else {
			//更新
			Question updateQuestion = new Question();
			updateQuestion.setGmtModified(System.currentTimeMillis());
			updateQuestion.setTitle(question.getTitle());
			updateQuestion.setDescription(question.getDescription());
			updateQuestion.setTag(question.getTag());
			QuestionExample example = new QuestionExample();
			example.createCriteria().andIdEqualTo(question.getId());
			int update = questionMapper.updateByExampleSelective(updateQuestion, example);
			if (update != 1){
				throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
			}
		}
    }

	public void incView(Long id) {
		Question question = new Question();
		question.setId(id);
		question.setViewCount(1);
		questionExtMapper.incView(question);
	}

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
		if (StringUtils.isBlank(queryDTO.getTag())){
			return new ArrayList<>();
		}else {
			String[] tags = StringUtils.split(queryDTO.getTag(),",");
			String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
			Question question = new Question();
			question.setId(queryDTO.getId());
			question.setTag(regexpTag);

			List<Question> questions = questionExtMapper.selectRelated(question);
			List<QuestionDTO> questionDTOS = questions.stream().map(q ->{
				QuestionDTO questionDTO  = new QuestionDTO();
				BeanUtils.copyProperties(q ,questionDTO);
				return questionDTO;
			}).collect(Collectors.toList());
			return questionDTOS;
		}
    }
}
