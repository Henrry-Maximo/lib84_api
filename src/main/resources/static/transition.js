const video = document.querySelector('.bg-video');

video.addEventListener('timeupdate', () => {
    if (!video.duration) return;

    const timeLeft = video.duration - video.currentTime;

    if (timeLeft <= 0.7) {
        video.style.opacity = '0';
    } else if (video.currentTime <= 0.7) {
        video.style.opacity = '1';
    }
});