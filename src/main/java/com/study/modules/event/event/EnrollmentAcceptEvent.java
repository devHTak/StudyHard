package com.study.modules.event.event;

import com.study.modules.event.Enrollment;

public class EnrollmentAcceptEvent extends EnrollmentEvent {
	
	public EnrollmentAcceptEvent() {
		super();
	}
	
	public EnrollmentAcceptEvent(Enrollment enrollment) {
		super(enrollment, enrollment.getEvent().getTitle() +" 모임 참가 신청이 수락되었습니다.");
	}

}
