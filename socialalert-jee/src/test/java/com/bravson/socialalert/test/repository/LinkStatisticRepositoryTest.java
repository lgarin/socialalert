package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.statistic.LinkStatisticEntity;
import com.bravson.socialalert.business.user.statistic.LinkStatisticRepository;
import com.bravson.socialalert.domain.media.statistic.PeriodInterval;
import com.bravson.socialalert.domain.user.statistic.LinkActivity;
import com.bravson.socialalert.domain.user.statistic.PeriodicLinkActivityCount;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LinkStatisticRepositoryTest extends BaseRepositoryTest {

	@Inject
	LinkStatisticRepository repository;
	
	@Test
    public void groupActivitiesByUser() {
		UserProfileEntity source = new UserProfileEntity("source");
		UserProfileEntity target = new UserProfileEntity("target");
		UserLinkEntity link = new UserLinkEntity(source, target);
		LinkStatisticEntity entity = new LinkStatisticEntity(link, LinkActivity.CREATE, createUserAccess("test", "1.2.3.4"));
    	persistAndIndex(entity);
    	
    	List<PeriodicLinkActivityCount> result = repository.groupLinkActivitiesByPeriod(entity.getTargetUserId(), entity.getActivity(), PeriodInterval.DAY);
    	Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
    	assertThat(result).containsExactly(new PeriodicLinkActivityCount(today, 1));
    }
}
