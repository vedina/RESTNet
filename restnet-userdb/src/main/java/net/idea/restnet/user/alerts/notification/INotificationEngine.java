package net.idea.restnet.user.alerts.notification;

import java.util.List;

import net.idea.restnet.b.Alert;
import net.idea.restnet.b.User;

public interface INotificationEngine {
    public boolean sendAlerts(User user, List<? extends Alert> alerts, String token) throws Exception;
}
