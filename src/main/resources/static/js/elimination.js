// window.onload = addListeners();
//
// function addListeners(){
//     document.getElementById('elimination').addEventListener('mousedown', mouseDown, false);
//     window.addEventListener('mouseup', mouseUp, false);
//
// }
//
// function mouseUp()
// {
//     window.removeEventListener('mousemove', divMove, true);
// }
//
// function mouseDown(e){
//     window.addEventListener('mousemove', divMove, true);
// }
//
// function divMove(e){
//     var div = document.getElementById('elimination');
//     div.style.position = 'absolute';
//     div.style.top = e.clientY + 'px';
//     div.style.left = e.clientX + 'px';
// }

$(function () {
    $("#elimination").draggable({
        scroll: true,
        appendTo: 'elimination-section',
        cursor: "move"
    });
});