package beans;

import db.JudgeScheduleDb;
import db.UserDb;
import entity.JudgeSchedule;
import entity.User;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author ryouhei
 */
@Named
@ViewScoped
public class C0020 extends SuperBb implements Serializable {
    @Inject
    JudgeScheduleDb judgeScheduleDb;
    @Inject
    UserDb userDb;
    
    private ScheduleModel model;

    private DefaultScheduleEvent event = new DefaultScheduleEvent();
    
    public C0020() {
        model = new LazyScheduleModel() {
            @Override
            public void loadEvents(LocalDateTime start, LocalDateTime end) {
                System.out.println(start);
                System.out.println(end);
                
                LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth());
                LocalDate endDate = LocalDate.of(end.getYear(), end.getMonth(), end.getDayOfMonth());
                List<JudgeSchedule> scheduleList = judgeScheduleDb.getSchedules(startDate, endDate);
                scheduleList.forEach(schedule -> {
                    // 審査員１
                    if (schedule.getJudgeUser1() != null) {
                        addEvent(DefaultScheduleEvent
                                .builder()
                                .title(schedule.getJudgeUser1().getName())
                                .startDate(schedule.getJudgeDate().atStartOfDay())
                                .endDate(schedule.getJudgeDate().plusDays(1).atStartOfDay())
                                .data(schedule.getJudgeUser1().getUserId())
                                .editable(false)
                                .allDay(true)
                                .build());
                    }
                    // 審査員２
                    if (schedule.getJudgeUser2() != null) {
                        addEvent(DefaultScheduleEvent
                                .builder()
                                .title(schedule.getJudgeUser2().getName())
                                .startDate(schedule.getJudgeDate().atStartOfDay())
                                .endDate(schedule.getJudgeDate().plusDays(1).atStartOfDay())
                                .data(schedule.getJudgeUser2().getUserId())
                                .editable(false)
                                .allDay(true)
                                .build());
                    }
                });
            }
        };
    }
    
    /**
     * イベント追加
     */
    public void addEvent() {
        // 入力されたユーザIDからユーザを検索
        User targetUser = userDb.search(event.getData().toString());
        if (targetUser != null) {
            LocalDate targetDate = LocalDate.of(event.getStartDate().getYear(), event.getStartDate().getMonth(), event.getStartDate().getDayOfMonth());
            JudgeSchedule judgeSchedule = judgeScheduleDb.search(targetDate);
            if (judgeSchedule == null) {
                // スケジュールがない場合は審査員１に設定する。
                JudgeSchedule newJudgeSchejule = new JudgeSchedule(targetDate, targetUser, null);
                judgeScheduleDb.add(newJudgeSchejule);
            } else {
                // スケジュールがある場合は審査員１と審査員２の空いている方に入れる。どちらも空いていない場合はエラー
                if (judgeSchedule.getJudgeUser1() == null) {
                    judgeSchedule.setJudgeUser1(targetUser);
                    judgeScheduleDb.update(judgeSchedule);
                } else if (judgeSchedule.getJudgeUser2() == null) {
                    judgeSchedule.setJudgeUser2(targetUser);
                    judgeScheduleDb.update(judgeSchedule);
                } else {
                    // 空きがないので登録不可
                    addMessage(FacesMessage.SEVERITY_ERROR, "1日に3名以上は登録できません。");
                    return;
                }
            }
        } else {
            // ユーザ存在しないときはエラー
            addMessage(FacesMessage.SEVERITY_ERROR, "該当のユーザは存在しません。");
            return;
        }
        
        // エラーがないときはイベントをカレンダーに登録
        // タイトルはユーザ名
        event.setTitle(targetUser.getName());
        // 日時範囲は選択した日の0:00から翌日の0:00を設定
        event.setEndDate(event.getStartDate().plusDays(1));
        // 全日とする
        event.setAllDay(true);
        // ドラッグで移動できないようにする
        event.setEditable(false);
 
        if(event.getId() == null)
            model.addEvent(event);
        else
            model.updateEvent(event);
        // 入力欄をリセットする。
        event = DefaultScheduleEvent.builder().startDate(event.getStartDate()).endDate(event.getStartDate().plusDays(1)).build();
        addMessage(FacesMessage.SEVERITY_INFO, "登録しました。");
    }
    
    /**
     * イベント削除
     */
    public void removeEvent() {
        // 入力されたユーザIDからユーザを検索
        User targetUser = userDb.search(event.getData().toString());
        if (targetUser != null) {
            LocalDate targetDate = LocalDate.of(event.getStartDate().getYear(), event.getStartDate().getMonth(), event.getStartDate().getDayOfMonth());
            JudgeSchedule judgeSchedule = judgeScheduleDb.search(targetDate);
            if (judgeSchedule != null) {
                if (judgeSchedule.getJudgeUser1() != null && judgeSchedule.getJudgeUser1().getUserId().equals(targetUser.getUserId())) {
                    judgeSchedule.setJudgeUser1(null);
                } else if (judgeSchedule.getJudgeUser2() != null && judgeSchedule.getJudgeUser2().getUserId().equals(targetUser.getUserId())) {
                    judgeSchedule.setJudgeUser2(null);
                }
                // 審査員１と２が両方空になった場合は行を削除する。
                // どちらかに設定されている場合はこうしんする。
                if (judgeSchedule.getJudgeUser1() == null && judgeSchedule.getJudgeUser2() == null) {
                    judgeScheduleDb.delete(judgeSchedule);
                } else {
                    judgeScheduleDb.update(judgeSchedule);
                }
                model.deleteEvent(event);
                event = new DefaultScheduleEvent();
                addMessage(FacesMessage.SEVERITY_INFO, "削除しました。");
            }
        }
    }
    
    /**
     * カレンダーのイベントをクリックしたとき
     * @param selectEvent 
     */
    public void onEventSelect(SelectEvent<DefaultScheduleEvent> selectEvent) {
        event = selectEvent.getObject();
    }
    
    /**
     * カレンダーのイベントが設定されていない部分をクリックしたとき
     * @param selectEvent 
     */
    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        // 日時範囲は選択した日の0:00から翌日の0:00を設定
        event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject()).endDate(selectEvent.getObject().plusDays(1)).build();
    }

    public ScheduleModel getModel() {
        return model;
    }

    public void setModel(ScheduleModel model) {
        this.model = model;
    }

    public DefaultScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(DefaultScheduleEvent event) {
        this.event = event;
    }
}
