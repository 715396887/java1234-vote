package com.java1234vote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 投票选项实体
 * @author java1234_小锋 （公众号：java1234）
 * @site www.java1234.vip
 * @company 南通小锋网络科技有限公司
 */
@TableName("t_vote_item")
@Data
public class VoteItem {

    private Integer id; // 编号

    private Integer voteId; // 投票ID

    private String name; // 投票选项名称

    private String image; // 投票选项图片

    private Integer number; // 票数

}
