package vn.giaihung.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.giaihung.jobhunter.domain.Job;
import vn.giaihung.jobhunter.domain.Skill;
import vn.giaihung.jobhunter.domain.Subscriber;
import vn.giaihung.jobhunter.domain.dto.response.email.EmailJobResDTO;
import vn.giaihung.jobhunter.domain.dto.response.email.EmailJobResDTO.CompanyEmail;
import vn.giaihung.jobhunter.domain.dto.response.email.EmailJobResDTO.SkillEmail;
import vn.giaihung.jobhunter.repository.JobRepository;
import vn.giaihung.jobhunter.repository.SkillRepository;
import vn.giaihung.jobhunter.repository.SubscriberRepository;
import vn.giaihung.jobhunter.utils.error.InvalidIdException;

@Service
@Transactional
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber handleCreateSubscriber(Subscriber postSubscriber) throws InvalidIdException {
        if (postSubscriber.getSkills() != null) {
            List<Long> skillIds = postSubscriber.getSkills().stream()
                    .map(skill -> skill.getId())
                    .toList();
            List<Skill> dbSkills = skillRepository.findByIdIn(skillIds);
            postSubscriber.setSkills(dbSkills);
        }

        if (subscriberRepository.existsByEmail(postSubscriber.getEmail())) {
            Subscriber dbSubscriber = subscriberRepository.findByEmail(postSubscriber.getEmail());
            List<String> alreadySubscribedSkills = new ArrayList<String>();
            for (Skill skill : postSubscriber.getSkills()) {
                if (dbSubscriber.getSkills().stream().anyMatch(currentSkill -> currentSkill.equals(skill))) {
                    alreadySubscribedSkills.add(skill.getName());
                }
            }
            if (alreadySubscribedSkills.size() > 0) {
                throw new InvalidIdException(
                        "You have already subscribed to the skill: " + alreadySubscribedSkills.toString());
            }
        }

        return subscriberRepository.save(postSubscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber putSubscriber) throws InvalidIdException {
        Optional<Subscriber> subscriberOptional = subscriberRepository.findById(putSubscriber.getId());

        if (!subscriberOptional.isPresent()) {
            throw new InvalidIdException("Subscriber with id: " + putSubscriber.getId() + " doesn't exist");
        }

        Subscriber currentSubscriber = subscriberOptional.get();
        if (putSubscriber.getSkills() != null) {
            List<Long> skillIds = putSubscriber.getSkills().stream()
                    .map(skill -> skill.getId())
                    .toList();
            List<Skill> dbSkills = skillRepository.findByIdIn(skillIds);
            currentSubscriber.setSkills(dbSkills);
        }

        return subscriberRepository.save(currentSubscriber);
    }

    private EmailJobResDTO convertToEmailRes(Job job) {
        EmailJobResDTO emailJobResDTO = new EmailJobResDTO();
        emailJobResDTO.setName(job.getName());
        emailJobResDTO.setCompany(new CompanyEmail(job.getCompany().getName()));
        emailJobResDTO.setSalary(job.getSalary());
        List<Skill> jobSkills = job.getSkills();
        List<SkillEmail> emailSkills = jobSkills.stream().map(skill -> new SkillEmail(skill.getName())).toList();
        emailJobResDTO.setSkills(emailSkills);
        return emailJobResDTO;
    }

    @Async
    public void handleSendEmailToSubscribers() {
        // Subscribers => Skills => Jobs => Send job using subscriber email
        List<Subscriber> subscribers = subscriberRepository.findAll();
        if (subscribers != null && subscribers.size() > 0) {
            for (Subscriber subscriber : subscribers) {
                List<Skill> skills = subscriber.getSkills();
                if (skills != null && skills.size() > 0) {
                    List<Job> jobs = jobRepository.findBySkillsIn(skills);
                    if (jobs != null && jobs.size() > 0) {
                        List<EmailJobResDTO> emailResList = jobs.stream().map(job -> convertToEmailRes(job)).toList();
                        emailService.sendEmailFromTemplateSync(subscriber.getEmail(),
                                "Cơ hội việc làm đang chờ đón bạn. Khám phá ngay!", "job", subscriber.getName(),
                                emailResList);
                    }
                }
            }
        }
    }

    @Async
    public void handleSendEmailToSubscribers(final String imageResourceName,
            final byte[] imageBytes, final String imageContentType) {
        // Subscribers => Skills => Jobs => Send job using subscriber email
        List<Subscriber> subscribers = subscriberRepository.findAll();
        if (subscribers != null && subscribers.size() > 0) {
            for (Subscriber subscriber : subscribers) {
                List<Skill> skills = subscriber.getSkills();
                if (skills != null && skills.size() > 0) {
                    List<Job> jobs = jobRepository.findBySkillsIn(skills);
                    if (jobs != null && jobs.size() > 0) {
                        List<EmailJobResDTO> emailResList = jobs.stream().map(job -> convertToEmailRes(job)).toList();
                        emailService.sendEmailFromTemplateSync(subscriber.getEmail(),
                                "Cơ hội việc làm đang chờ đón bạn. Khám phá ngay!", "job", subscriber.getName(),
                                emailResList, imageResourceName, imageBytes, imageContentType);
                    }
                }
            }
        }
    }

    public Subscriber handleGetSubscriberByEmail(String email) {
        return subscriberRepository.findByEmail(email);
    }
}
