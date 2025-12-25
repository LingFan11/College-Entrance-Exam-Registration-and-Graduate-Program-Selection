package com.lingfan.xspp.grad.service.policy;

import com.lingfan.xspp.grad.entity.GradMentorRequirement;
import com.lingfan.xspp.grad.entity.GradStudentProfile;

/**
 * 科目门槛检查策略 (Requirements 3.1, 3.2, 3.3, 3.4)
 * 检查学生的考研初试各科目成绩是否达到导师设置的最低分要求
 */
public class SubjectThresholdPolicy implements RecommendPolicy {

    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        GradMentorRequirement req = ctx.getRequirement();
        if (req == null) {
            return;
        }

        GradStudentProfile profile = ctx.getProfile();
        
        // 检查总分门槛
        checkTotalScore(profile, req, acc);
        
        // 检查政治门槛
        checkPoliticsScore(profile, req, acc);
        
        // 检查英语考试门槛
        checkEnglishExamScore(profile, req, acc);
        
        // 检查数学门槛
        checkMathScore(profile, req, acc);
        
        // 检查专业课门槛
        checkProfessionalScore(profile, req, acc);
    }

    /**
     * 检查总分是否达到门槛 (Requirements 3.1, 3.2)
     */
    private void checkTotalScore(GradStudentProfile profile, GradMentorRequirement req, ScoreAccumulator acc) {
        Integer minTotal = req.getMinTotalScore();
        if (minTotal == null || minTotal <= 0) {
            return;
        }

        Integer studentTotal = profile.getExamTotalScore();
        if (studentTotal == null) {
            // 缺失成绩，应用惩罚分 (Requirements 3.4)
            acc.addReason("缺少初试总分，无法判断是否达到门槛 " + minTotal);
            acc.setStatusAtLeast("borderline");
            acc.addScore(-10.0);
        } else if (studentTotal < minTotal) {
            // 不达标 (Requirements 3.2)
            acc.addReason("初试总分不足，要求最低 " + minTotal + "，实际 " + studentTotal);
            acc.setStatusAtLeast("unfit");
        }
    }

    /**
     * 检查政治成绩是否达到门槛 (Requirements 3.1, 3.2)
     */
    private void checkPoliticsScore(GradStudentProfile profile, GradMentorRequirement req, ScoreAccumulator acc) {
        Integer minPolitics = req.getMinPolitics();
        if (minPolitics == null || minPolitics <= 0) {
            return;
        }

        Integer studentPolitics = profile.getPoliticsScore();
        if (studentPolitics == null) {
            // 缺失成绩，应用惩罚分 (Requirements 3.4)
            acc.addReason("缺少政治成绩，无法判断是否达到门槛 " + minPolitics);
            acc.setStatusAtLeast("borderline");
            acc.addScore(-10.0);
        } else if (studentPolitics < minPolitics) {
            // 不达标 (Requirements 3.2)
            acc.addReason("政治成绩不足，要求最低 " + minPolitics + "，实际 " + studentPolitics);
            acc.setStatusAtLeast("unfit");
        }
    }

    /**
     * 检查英语考试成绩是否达到门槛 (Requirements 3.1, 3.2)
     */
    private void checkEnglishExamScore(GradStudentProfile profile, GradMentorRequirement req, ScoreAccumulator acc) {
        Integer minEnglishExam = req.getMinEnglishExam();
        if (minEnglishExam == null || minEnglishExam <= 0) {
            return;
        }

        Integer studentEnglishExam = profile.getEnglishExamScore();
        if (studentEnglishExam == null) {
            // 缺失成绩，应用惩罚分 (Requirements 3.4)
            acc.addReason("缺少英语考试成绩，无法判断是否达到门槛 " + minEnglishExam);
            acc.setStatusAtLeast("borderline");
            acc.addScore(-10.0);
        } else if (studentEnglishExam < minEnglishExam) {
            // 不达标 (Requirements 3.2)
            acc.addReason("英语考试成绩不足，要求最低 " + minEnglishExam + "，实际 " + studentEnglishExam);
            acc.setStatusAtLeast("unfit");
        }
    }

    /**
     * 检查数学成绩是否达到门槛 (Requirements 3.1, 3.2)
     */
    private void checkMathScore(GradStudentProfile profile, GradMentorRequirement req, ScoreAccumulator acc) {
        Integer minMath = req.getMinMath();
        if (minMath == null || minMath <= 0) {
            return;
        }

        Integer studentMath = profile.getMathScore();
        if (studentMath == null) {
            // 缺失成绩，应用惩罚分 (Requirements 3.4)
            acc.addReason("缺少数学成绩，无法判断是否达到门槛 " + minMath);
            acc.setStatusAtLeast("borderline");
            acc.addScore(-10.0);
        } else if (studentMath < minMath) {
            // 不达标 (Requirements 3.2)
            acc.addReason("数学成绩不足，要求最低 " + minMath + "，实际 " + studentMath);
            acc.setStatusAtLeast("unfit");
        }
    }

    /**
     * 检查专业课成绩是否达到门槛 (Requirements 3.1, 3.2)
     */
    private void checkProfessionalScore(GradStudentProfile profile, GradMentorRequirement req, ScoreAccumulator acc) {
        Integer minProfessional = req.getMinProfessional();
        if (minProfessional == null || minProfessional <= 0) {
            return;
        }

        Integer studentProfessional = profile.getProfessionalScore();
        if (studentProfessional == null) {
            // 缺失成绩，应用惩罚分 (Requirements 3.4)
            acc.addReason("缺少专业课成绩，无法判断是否达到门槛 " + minProfessional);
            acc.setStatusAtLeast("borderline");
            acc.addScore(-10.0);
        } else if (studentProfessional < minProfessional) {
            // 不达标 (Requirements 3.2)
            acc.addReason("专业课成绩不足，要求最低 " + minProfessional + "，实际 " + studentProfessional);
            acc.setStatusAtLeast("unfit");
        }
    }

    @Override
    public String name() {
        return "SubjectThresholdPolicy";
    }
}
