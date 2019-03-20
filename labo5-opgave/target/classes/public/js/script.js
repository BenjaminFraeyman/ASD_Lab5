(function () {
    // OnLoad
    reload();

    $('.type input[type="radio"]').on('change', handleTypeSwitch);
    toggleSaveButton(false);

})()


function reload() {
    // Get all notes here
    var notes = new NotesApi();

    // Remove existing notes
    notes.list().then(function (result) {
        addAndRemove(result.sort(function (a, b) {
            return a.title.toLowerCase().localeCompare(b.title.toLowerCase());
        }));
    });
}

function addAndRemove(newNotes) {
    $('#notes').children().remove();

    $(newNotes).forEach(function(note) {
        $('#notes').append(createNote(note));
    });
}


function handleTypeSwitch(ev) {
    switch (ev.target.value) {
        case 'note':
            setTypeNote();
            break;
        case 'checklist':
            setTypeTodo();
    }
}

function setTypeNote() {
    $('#content-note').css('display', 'table');
    $('#content-checklist').css('display', 'none');
    $('#content-add-todo').css('display', 'none');
    if (!$('#typeNote').prop('checked')) {
        $('#typeTodo').prop('checked', false);
        $('#typeNote').prop('checked', true);
    }
}

function setTypeTodo() {
    $('#content-note').css('display', 'none');
    $('#content-checklist').css('display', 'block');
    $('#content-add-todo').css('display', 'block');
    if (!$('#typeTodo').prop('checked')) {
        $('#typeNote').prop('checked', false);
        $('#typeTodo').prop('checked', true);
    }
}

function resetForm() {
    var form = $('#myForm')[0];
    form.reset();
    toggleSaveButton(false);
    $('#content-checklist').children().forEach(function (item) {
        if ($(item).find('[name="itemCheck[0]"]').length !== 1) {
            item.remove();
        }
    });
    setTypeNote();
}

function postForm(event, id) {
    event.preventDefault();
    event.stopPropagation();
    var notes = new NotesApi();
    var form = $('#myForm')[0];
    var type = form.type.value;
    var title = form.title.value;
    var content = $('#content-note textarea').get(0).value;



    if (type === 'note') {
        var note = {
            title: title,
            content: content
        };
    } else if (type === 'checklist') {
        var amount = $('#content-checklist').children().length;
        var todos = [];
        for (var i = 0; i < amount; i++) {
            var checked = form.elements['itemCheck[' + i + ']'].checked;
            var label = form.elements['itemLabel[' + i + ']'].value;
            if (label && label !== '') {
                todos.push({
                    label: label,
                    checked: checked
                });
            }
        }
        var note = {
            title: title,
            content: todos
        };
    }

    if (note.title.trim() === '' ||
        ((!$.isArray(note.content) && note.content.trim() === '') ||
            ($.isArray(note.content) && note.content.length === 0))) {
        return;
    }


    if (id) {
        notes.update(id, note).then(function () {
            reload();
            resetForm();
        });
    } else {
        notes.create(note).then(function () {
            reload();
            resetForm();
        });
    }

}

function deleteNote(id) {
    var notes = new NotesApi();
    notes.delete(id).then(function () {
        reload();
    });
}

function toggleSaveButton(editMode, id) {
    if (editMode) {
        $('#saveButton').html('Update');
        $('#myForm').off('submit');
        $('#myForm').on('submit', function (ev) {
            postForm(ev, id);
        });
    } else {
        $('#saveButton').html('Create');
        $('#myForm').off('submit');
        $('#myForm').on('submit', postForm);
    }
}

function editNote(note) {
    resetForm();
    toggleSaveButton(true, note.id);
    var isTodo = $.isArray(note.content);
    var form = $('#myForm')[0];
    $(form.title).val(note.title);
    var content = $('#content-note textarea').get(0).value;

    if ($.isArray(note.content)) {
        // ToDo list
        setTypeTodo();
        note.content.forEach(function (item, idx) {
            if (idx > 0) {
                addToDo();
            }
            $(document.getElementById('itemCheck[' + idx + ']')).prop('checked', item.checked);
            $(document.getElementById('itemLabel[' + idx + ']')).val(item.label);
        });
    } else {
        // Normal note
        setTypeNote();
        $($('#content-note textarea').get(0)).val(note.content);
    }
}

function addToDo() {
    var idx = $('#content-checklist div.input-group').length;
    $('#content-checklist').append(createTodo(idx));
}




/* API METHODS */

/** Constructor for NotesApi, call with new NotesApi() */
function NotesApi() {
    var host = '/api/notes';
    return {
        /** List all notes */
        list: function () {
            return axios.get(host)
                .then(function (response) {
                    return response.data;
                })
                .catch(function (error) {
                    return error;
                });
        },

        /** Create a new note */
        create: function (note) {
            return axios.post(host, note)
                .then(function (response) {
                    return response.data;
                })
                .catch(function (error) {
                    return error;
                });
        },

        /** Updates an existing note */
        update: function (id, note) {
            return axios.put([host, id].join('/'), note)
                .then(function (response) {
                    return response.data;
                })
                .catch(function (error) {
                    return error;
                });
        },
        delete: function (id) {
            return axios.delete([host, id].join('/'))
                .then(function (response) {
                    return response.data;
                })
                .catch(function (error) {
                    return error;
                });
        }
    };
}



/* DOM MANIPULATION METHODS */

/** Creates a note based on a node object from the server */
function createNote(note) {
    var noteDiv = $(document.createElement('div'));
    var titleDiv = $(document.createElement('div'));
    var rowsDiv = $(document.createElement('div'));
    noteDiv.addClass('note');
    noteDiv.attr('id', note.id);
    rowsDiv.addClass('rows');
    titleDiv.addClass('title');
    titleDiv.html(note.title);

    // Edit button
    var editButton = $(document.createElement('span'));
    editButton.addClass('pull-left edit-btn');
    var icon = $(document.createElement('span'));
    icon.addClass("glyphicon glyphicon-edit");
    editButton.on('click', function () {
        editNote(note);
    });
    editButton.append(icon);

    // Close button
    var closeButton = $(document.createElement('button'));
    closeButton.attr('type', 'button');
    closeButton.addClass('close');
    closeButton.html('&times;');
    closeButton.on('click', function () {
        deleteNote(note.id);
    });
    titleDiv.append(editButton);
    titleDiv.append(closeButton);
    // Append the title
    noteDiv.append(titleDiv);
    // Append the content
    if ($.isArray(note.content)) {
        // Check list
        note.content.forEach(function (element) {
            var rowDiv = $(document.createElement('div'));
            var iconSpan = $(document.createElement('span'));
            var textSpan = $(document.createElement('span'));
            rowDiv.addClass('myrow');
            iconSpan.addClass('icon');
            var c = '<span class="glyphicon glyphicon-check"></span>';
            var uc = '<span class="glyphicon glyphicon-unchecked"></span>';
            iconSpan.html(element.checked ? c : uc);
            textSpan.addClass('item');
            textSpan.html(element.label);
            rowDiv.append(iconSpan);
            rowDiv.append(textSpan);
            rowsDiv.append(rowDiv);
        });
        noteDiv.append(rowsDiv);
    } else {
        // Simple note
        var rowDiv = $(document.createElement('div'));
        rowDiv.addClass('myrow');
        rowDiv.html(note.content);
        rowsDiv.append(rowDiv);
        noteDiv.append(rowsDiv);
    }
    return noteDiv;
}

function createTodo(idx) {
    var inputGrpDiv = $(document.createElement('div'));
    var inputGrpAddonSpan = $(document.createElement('span'));
    var input = $(document.createElement('input'));
    var inputTxt = $(document.createElement('input'));
    inputGrpDiv.addClass('input-group input-group-sm');
    inputGrpAddonSpan.addClass('input-group-addon');
    input.attr('type', 'checkbox');
    input.attr('name', 'itemCheck[' + idx + ']');
    input.attr('id', 'itemCheck[' + idx + ']');
    inputTxt.attr('type', 'text');
    inputTxt.attr('name', 'itemLabel[' + idx + ']');
    inputTxt.attr('id', 'itemLabel[' + idx + ']');
    inputTxt.attr('autocomplete', 'off');
    inputTxt.addClass('form-control');

    inputGrpAddonSpan.append(input);
    inputGrpDiv.append(inputGrpAddonSpan);
    inputGrpDiv.append(inputTxt);
    return inputGrpDiv;
}