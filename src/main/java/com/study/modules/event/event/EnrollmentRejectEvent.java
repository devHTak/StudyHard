package com.study.modules.event.event;

import com.study.modules.event.Enrollment;

public class EnrollmentRejectEvent extends EnrollmentEvent {
	
	public EnrollmentRejectEvent() {
		super();
	}
	
	public EnrollmentRejectEvent(Enrollment enrollment) {
		super(enrollment, enrollment.getEvent().getTitle() +" 모임에 거절되었습니다.");
	}

}
