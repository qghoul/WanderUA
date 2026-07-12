document.addEventListener('DOMContentLoaded', function() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    
    stompClient.heartbeat.outgoing = 0;
    stompClient.heartbeat.incoming = 0;
    
    stompClient.connect({}, function() {
        stompClient.subscribe('/topic/notifications', function(message) {
            showNotification(message.body);
        });
    }, function(error) {
        console.error('STOMP connection error: ', error);
    });

    function showNotification(message) {
        const notification = document.createElement('div');
        notification.className = 'notification';
        
        let type = 'info';
        if (message.includes('підтверджено') || message.includes('затверджено')) {
            type = 'success';
        } else if (message.includes('відхилено')) {
            type = 'error';
        }
        notification.classList.add(type);
        
        notification.innerHTML = `
            <span class="notification-icon">
                ${type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ️'}
            </span>
            <span>${message}</span>
        `;
        
        document.getElementById('notification-container').appendChild(notification);
        
        setTimeout(() => {
            notification.remove();
        }, 9000);
    }
    
    stompClient.onclose = function() {
        setTimeout(() => {
            location.reload();
        }, 9000);
    };
});