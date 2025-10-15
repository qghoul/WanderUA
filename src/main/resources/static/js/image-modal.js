// Image modal functions
function openImageModal(imageUrl, galleryUrls = null) {
    const modal = document.getElementById('imageModal');

    if (galleryUrls && Array.isArray(galleryUrls)) {
        currentImageGallery = galleryUrls;
        currentImageIndex = galleryUrls.indexOf(imageUrl);
    } else {
        currentImageGallery = [imageUrl];
        currentImageIndex = 0;
    }

    updateModalImage();
    modal.style.display = 'block';
    document.body.style.overflow = 'hidden';
}

function closeImageModal() {
    const modal = document.getElementById('imageModal');
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
    currentImageGallery = [];
    currentImageIndex = 0;
}

function updateModalImage() {
    const modalImage = document.getElementById('modalImage');
    const imageCounter = document.getElementById('imageCounter');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    modalImage.src = currentImageGallery[currentImageIndex];

    if (currentImageGallery.length > 1) {
        imageCounter.textContent = `${currentImageIndex + 1} / ${currentImageGallery.length}`;
        imageCounter.style.display = 'block';
        prevBtn.style.display = 'block';
        nextBtn.style.display = 'block';
        prevBtn.disabled = currentImageIndex === 0;
        nextBtn.disabled = currentImageIndex === currentImageGallery.length - 1;
    } else {
        imageCounter.style.display = 'none';
        prevBtn.style.display = 'none';
        nextBtn.style.display = 'none';
    }
}

function previousImage() {
    if (currentImageIndex > 0) {
        currentImageIndex--;
        updateModalImage();
    }
}

function nextImage() {
    if (currentImageIndex < currentImageGallery.length - 1) {
        currentImageIndex++;
        updateModalImage();
    }
}

// Keyboard navigation for image modal
document.addEventListener('keydown', function(e) {
    const modal = document.getElementById('imageModal');
    if (modal.style.display === 'block') {
        if (e.key === 'Escape') {
            closeImageModal();
        } else if (e.key === 'ArrowLeft') {
            previousImage();
        } else if (e.key === 'ArrowRight') {
            nextImage();
        }
    }
});