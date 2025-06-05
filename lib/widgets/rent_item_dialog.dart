import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:kreisel_frontend/models/item_model.dart';
import 'package:kreisel_frontend/services/api_service.dart';

class RentItemDialog extends StatefulWidget {
  final Item item;
  final VoidCallback onRented;

  const RentItemDialog({Key? key, required this.item, required this.onRented})
    : super(key: key);

  @override
  _RentItemDialogState createState() => _RentItemDialogState();
}

class _RentItemDialogState extends State<RentItemDialog> {
  final TextEditingController _notesController = TextEditingController();
  DateTime _selectedDate = DateTime.now().add(Duration(days: 7));
  bool _isLoading = false;
  bool _agreedToTerms = false;

  @override
  void dispose() {
    _notesController.dispose();
    super.dispose();
  }

  Future<void> _submitRental() async {
    if (!_validateForm()) return;

    setState(() => _isLoading = true);

    try {
      // Format date as YYYY-MM-DD for API
      final formattedDate =
          "${_selectedDate.year}-${_selectedDate.month.toString().padLeft(2, '0')}-${_selectedDate.day.toString().padLeft(2, '0')}";

      await ApiService.rentItem(widget.item.id, formattedDate);

      if (mounted) {
        Navigator.pop(context);
        widget.onRented();
        _showSuccessDialog();
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        _showErrorDialog(e.toString());
      }
    }
  }

  bool _validateForm() {
    if (!_agreedToTerms) {
      _showErrorDialog('Bitte stimme den Ausleihbedingungen zu.');
      return false;
    }

    if (_selectedDate.isBefore(DateTime.now())) {
      _showErrorDialog(
        'Das Rückgabedatum darf nicht in der Vergangenheit liegen.',
      );
      return false;
    }

    return true;
  }

  void _showDatePicker() {
    showCupertinoModalPopup(
      context: context,
      builder:
          (context) => Container(
            height: 250,
            color: Color(0xFF1C1C1E),
            child: Column(
              children: [
                Container(
                  padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      CupertinoButton(
                        child: Text('Abbrechen'),
                        onPressed: () => Navigator.pop(context),
                      ),
                      CupertinoButton(
                        child: Text('Fertig'),
                        onPressed: () => Navigator.pop(context),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  child: CupertinoDatePicker(
                    mode: CupertinoDatePickerMode.date,
                    initialDateTime: _selectedDate,
                    minimumDate: DateTime.now(),
                    maximumDate: DateTime.now().add(Duration(days: 365)),
                    onDateTimeChanged: (DateTime date) {
                      setState(() => _selectedDate = date);
                    },
                  ),
                ),
              ],
            ),
          ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoAlertDialog(
      title: Text(
        'Item ausleihen',
        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
      ),
      content: Container(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(height: 16),
            // Item Info
            Container(
              padding: EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Color(0xFF2C2C2E),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.item.name,
                    style: TextStyle(fontWeight: FontWeight.w600, fontSize: 16),
                  ),
                  if (widget.item.brand != null) ...[
                    SizedBox(height: 4),
                    Text(
                      widget.item.brand!,
                      style: TextStyle(color: Colors.grey, fontSize: 14),
                    ),
                  ],
                  if (widget.item.size != null) ...[
                    SizedBox(height: 4),
                    Text(
                      'Größe: ${widget.item.size}',
                      style: TextStyle(color: Colors.grey, fontSize: 14),
                    ),
                  ],
                  SizedBox(height: 4),
                  Text(
                    'Zustand: ${widget.item.zustand}',
                    style: TextStyle(color: Colors.grey, fontSize: 14),
                  ),
                ],
              ),
            ),

            SizedBox(height: 16),

            // Rückgabedatum
            Text(
              'Geplantes Rückgabedatum *',
              style: TextStyle(fontWeight: FontWeight.w500),
            ),
            SizedBox(height: 8),
            GestureDetector(
              onTap: _showDatePicker,
              child: Container(
                padding: EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Color(0xFF2C2C2E),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '${_selectedDate.day}.${_selectedDate.month}.${_selectedDate.year}',
                      style: TextStyle(color: Colors.white),
                    ),
                    Icon(CupertinoIcons.calendar, color: Colors.grey, size: 20),
                  ],
                ),
              ),
            ),

            SizedBox(height: 16),

            // Notizen
            Text(
              'Anmerkungen (optional)',
              style: TextStyle(fontWeight: FontWeight.w500),
            ),
            SizedBox(height: 8),
            CupertinoTextField(
              controller: _notesController,
              placeholder: 'Besondere Wünsche oder Anmerkungen...',
              maxLines: 3,
              decoration: BoxDecoration(
                color: Color(0xFF2C2C2E),
                borderRadius: BorderRadius.circular(8),
              ),
              style: TextStyle(color: Colors.white),
              placeholderStyle: TextStyle(color: Colors.grey),
            ),

            SizedBox(height: 16),

            // Terms Agreement
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CupertinoButton(
                  padding: EdgeInsets.zero,
                  onPressed: () {
                    setState(() => _agreedToTerms = !_agreedToTerms);
                  },
                  child: Icon(
                    _agreedToTerms
                        ? CupertinoIcons.checkmark_square_fill
                        : CupertinoIcons.square,
                    color: _agreedToTerms ? Color(0xFF007AFF) : Colors.grey,
                    size: 20,
                  ),
                ),
                SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'Ich stimme den Ausleihbedingungen zu und verpflichte mich, das Item in gutem Zustand zurückzugeben.',
                    style: TextStyle(fontSize: 12, color: Colors.grey),
                  ),
                ),
              ],
            ),

            SizedBox(height: 8),

            // Hinweis
            Container(
              padding: EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Color(0xFFFF9500).withOpacity(0.1),
                borderRadius: BorderRadius.circular(6),
                border: Border.all(color: Color(0xFFFF9500).withOpacity(0.3)),
              ),
              child: Text(
                'Du erhältst eine Bestätigung per E-Mail mit weiteren Informationen zur Abholung.',
                style: TextStyle(fontSize: 11, color: Color(0xFFFF9500)),
              ),
            ),
          ],
        ),
      ),
      actions: [
        CupertinoDialogAction(
          child: Text('Abbrechen'),
          onPressed: () => Navigator.pop(context),
        ),
        CupertinoDialogAction(
          isDefaultAction: true,
          onPressed: _isLoading ? null : _submitRental,
          child:
              _isLoading
                  ? CupertinoActivityIndicator()
                  : Text(
                    'Ausleihen',
                    style: TextStyle(fontWeight: FontWeight.w600),
                  ),
        ),
      ],
    );
  }

  void _showSuccessDialog() {
    showCupertinoDialog(
      context: context,
      builder:
          (context) => CupertinoAlertDialog(
            title: Text('Erfolgreich!'),
            content: Text(
              'Deine Ausleihanfrage wurde gesendet. Du erhältst in Kürze eine Bestätigung.',
            ),
            actions: [
              CupertinoDialogAction(
                child: Text('OK'),
                onPressed: () => Navigator.pop(context),
              ),
            ],
          ),
    );
  }

  void _showErrorDialog(String message) {
    showCupertinoDialog(
      context: context,
      builder:
          (context) => CupertinoAlertDialog(
            title: Text('Fehler'),
            content: Text(message),
            actions: [
              CupertinoDialogAction(
                child: Text('OK'),
                onPressed: () => Navigator.pop(context),
              ),
            ],
          ),
    );
  }
}
