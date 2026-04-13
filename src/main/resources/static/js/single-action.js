(function () {
    function disableButton(button, pendingText) {
        if (!button) {
            return;
        }
        if (!button.dataset.originalText) {
            button.dataset.originalText = button.textContent;
        }
        button.disabled = true;
        button.classList.add('is-pending');
        if (pendingText) {
            button.textContent = pendingText;
        }
    }

    function enableButton(button) {
        if (!button) {
            return;
        }
        button.disabled = false;
        button.classList.remove('is-pending');
        if (button.dataset.originalText) {
            button.textContent = button.dataset.originalText;
        }
    }

    function markFormPending(form, submitter) {
        if (!form || form.dataset.pending === 'true') {
            return false;
        }
        form.dataset.pending = 'true';
        const button = submitter || form.querySelector('button[type="submit"], input[type="submit"]');
        disableButton(button, button?.dataset.pendingText);
        return true;
    }

    document.addEventListener('submit', event => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement) || !form.matches('[data-single-submit]')) {
            return;
        }
        const submitter = event.submitter || null;
        if (!markFormPending(form, submitter)) {
            event.preventDefault();
        }
    }, true);

    document.addEventListener('click', event => {
        const button = event.target.closest('[data-click-once]');
        if (!button) {
            return;
        }
        if (button.dataset.pending === 'true') {
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        button.dataset.pending = 'true';
        disableButton(button, button.dataset.pendingText);
    }, true);

    window.SingleAction = {
        start(button, pendingText) {
            if (!button || button.dataset.pending === 'true') {
                return false;
            }
            button.dataset.pending = 'true';
            disableButton(button, pendingText || button.dataset.pendingText);
            return true;
        },
        finish(button) {
            if (!button) {
                return;
            }
            button.dataset.pending = 'false';
            enableButton(button);
        },
        markFormPending
    };
})();
