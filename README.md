# 肯迪软件工作室
# financial(财务软件2.0)
## 一个尝试代替Excel做业务功能的Java桌面应用
## 背景介绍
    该公司之前的大部分财务工作是用Excel来完成的，每天最高会处理600个Excel文件，Excel页签个数多达750个。各个页签之间都是单元格公式进行相互关联。当相互之间的关联越来越多时，
    Excel的打开就会变得很慢，严重影响工作效率，也不利于业务的开展。因此开发一款可以替代Excel，完成财务统计工作的软件就称为迫切的需求。因此，
    财务软件应运而生。
## 建设目标
    开发一个可以支撑目前Excel汇总统计工作所有功能的财务软件。
    Excel能做的，财务软件也要能做：如总汇、个人当天结算、团队回水、前后表的衔接。
## 功能迁移
    Excel能做的，财务软件也要能做：如总汇、个人当天结算、团队回水、前后表的衔接。
## 部分效果
![](https://github.com/greatkendy123/financial/raw/master/resource/images/1.png)
![](https://github.com/greatkendy123/financial/raw/master/resource/images/2.png)
![](https://github.com/greatkendy123/financial/raw/master/resource/images/3.png)

## 代码示例（自动配额）
	/**
	* 联盟自动配额
	* 这是本控制类最核心的代码
	* 算法：找剩余值中的两个最大最小值进行一方清零，不断循环
	* 
	* @time 2017年12月18日
	*/
    public void autoQuota() {
    	
    	if(tableQuota.getItems() == null || tableQuota.getItems().size()==0) {
    		return;
    	}
    	
    	boolean isDone = false;
    	int count = 0;
    	while(!isDone) {
    		count++;
    		ClubQuota row1 = getRecord(1);
			ClubQuota row2 = getRecord(0);
			
			//log.info(String.format("最大值：%s::%s", row1.getQuotaClubName(),row1.getQuotaRest()));
			//log.info(String.format("最小值：%s::%s", row2.getQuotaClubName(),row2.getQuotaRest()));
			
			Double first = NumUtil.getNum(row1.getQuotaRest());
			Double  second= NumUtil.getNum(row2.getQuotaRest());
			if(first * second >= 0) {
				isDone = true;
				log.info("=====================联盟配额结束！count:"+(count-1));
				break;
			}
			//转换（row1永远是绝对值的大数，row2是绝对值的小数	）
			if( Double.compare(first, second) > 0 ) {
				if( Double.compare(Math.abs(first), Math.abs(second)) < 0 ) {
					ClubQuota tempRow ;
					tempRow = row1;
					row1 = row2;
					row2 = tempRow;
					Double tempVal ;
					tempVal = first;
					first = second;
					second = tempVal;
					//log.info(String.format("转换最大值：%s::%s", row1.getQuotaClubName(),row1.getQuotaRest()));
					//log.info(String.format("转换最小值：%s::%s", row2.getQuotaClubName(),row2.getQuotaRest()));
				}
			}
			
			//绝对值大数行设值
			if(StringUtil.isBlank(row1.getQuotaHedgeFirst())) {
				row1.setQuotaHedgeFirst(row2.getQuotaRest());
			}else if(StringUtil.isBlank(row1.getQuotaHedgeSecond())) {
				row1.setQuotaHedgeSecond(row2.getQuotaRest());
			}else if(StringUtil.isBlank(row1.getQuotaHedgeThree())) {
				row1.setQuotaHedgeThree(row2.getQuotaRest());
			}else if(StringUtil.isBlank(row1.getQuotaHedgeFour())) {
				row1.setQuotaHedgeFour(row2.getQuotaRest());
			}else if(StringUtil.isBlank(row1.getQuotaHedgeFive())) {
				row1.setQuotaHedgeFive(row2.getQuotaRest());
			}
			row1.setQuotaRest(NumUtil.digit0(first+second));
			//绝对值小数行设值
			String small = NumUtil.digit0(second * (-1));
			if(StringUtil.isBlank(row2.getQuotaHedgeFirst())) {
				row2.setQuotaHedgeFirst(small);
			}else if(StringUtil.isBlank(row2.getQuotaHedgeSecond())) {
				row2.setQuotaHedgeSecond(small);
			}else if(StringUtil.isBlank(row2.getQuotaHedgeThree())) {
				row2.setQuotaHedgeThree(small);
			}else if(StringUtil.isBlank(row2.getQuotaHedgeFour())) {
				row2.setQuotaHedgeFour(small);
			}else if(StringUtil.isBlank(row2.getQuotaHedgeFive())) {
				row2.setQuotaHedgeFive(small);
			}
			row2.setQuotaRest("0");
			//以下做其他逻辑
			//输出钱由输者转给赢者
			String from , to , money = "";
			ClubQuota winner ;
			if(small.contains("-")) {
				from  = row1.getQuotaClubName();
				to = row2.getQuotaClubName();
				money = Integer.valueOf(small.replace("-", "")).toString();
				winner = row2;
			}else {
				from  = row2.getQuotaClubName();
				to = row1.getQuotaClubName();
				money = small;
				winner = row1;
			}
			log.info(String.format("%s转%s到%s", from,money,to));
			addRecord2TableQuotaPay(new QuotaMoneyInfo(winner.getQuotaClubId(),from,money,to));
			
		}
    	tableQuotaPay.refresh();
		tableQuota.refresh();
		//配额最后还有剩余为负数的则全部结转到银河ATM
		addNegativeRest2ATM();
    	
    }
    
    /**
     * 配额最后还有剩余为负数的则全部结转到银河ATM
     * @time 2017年12月18日
     */
    private void addNegativeRest2ATM() {
    	tableQuota.getItems()
    		.parallelStream()
    		.filter( info -> NumUtil.getNum(info.getQuotaRest()) < 0)
    		.forEach( info -> {
    			String from , to , money = "";
    			Club winnerClub = allClubMap.get("555551");//555551为银河ATM的俱乐部ID
    			to = winnerClub.getName();		
    			money = NumUtil.digit0((-1) * NumUtil.getNum(info.getQuotaRest()));
    			from = info.getQuotaClubName();
    			//log.info(String.format("%s转%s到%s", from,money,to));
    			addRecord2TableQuotaPay(new QuotaMoneyInfo(winnerClub.getClubId(),from,money,to));
    		});
    	tableQuotaPay.refresh();
    }
    
    /**
     * 往结账表新增一条记录
     * 应用场景：自动配额时，每配额一次就产生一条记录
     * @time 2017年12月18日
     * @param info
     */
    private void addRecord2TableQuotaPay(QuotaMoneyInfo info) {
    	ObservableList<QuotaMoneyInfo> obList = tableQuotaPay.getItems();
    	if(obList == null)  obList = FXCollections.observableArrayList();
    	obList.add(info);
    	tableQuotaPay.setItems(obList);
    }
    
	/**
	 * 获取剩余最大和最小的两行
	 * @time 2017年12月16日
	 * @param type 0:最小值  1：最大值 
	 * @return
	 */
    private ClubQuota getRecord(int type) {
    	if(1 ==  type )
	    	return tableQuota.getItems().parallelStream().max(new Comparator<ClubQuota>() {  
	            @Override  
	            public int compare(ClubQuota o1, ClubQuota o2) {  
	                 return NumUtil.getNum(o1.getQuotaRest()).compareTo(NumUtil.getNum(o2.getQuotaRest()));  
	            }  
	        }).get();  
    	else
	    	return tableQuota.getItems().parallelStream().min(new Comparator<ClubQuota>() {  
	            @Override  
	            public int compare(ClubQuota o1, ClubQuota o2) {  
	                 return NumUtil.getNum(o1.getQuotaRest()).compareTo(NumUtil.getNum(o2.getQuotaRest()));  
	            }  
	        }).get();  
    }
