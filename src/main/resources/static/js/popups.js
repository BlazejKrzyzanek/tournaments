function openForm(id) {
    document.getElementById(id).style.display = "block";
    document.getElementById(id).style.opacity = "100%";
    document.getElementById('wrap').style.filter = 'blur(8px)';
    document.getElementById('wrap').style.opacity = '80%';
}

function closeForm(id) {
    document.getElementById(id).style.display = "none";
    document.getElementById(id).style.opacity = "0";
    document.getElementById('wrap').style.filter = 'none';
    document.getElementById('wrap').style.opacity = '100%';
}