﻿/*
 * Copyright (c) 2015 Nikolay Zapolnov (zapolnov@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
#include "MainWindow.h"
#include "NewRuleDialog.h"
#include "BuilderFactory.h"
#include <QMessageBox>
#include <QFileDialog>
#include <QCloseEvent>

MainWindow::MainWindow(QWidget* parent)
    : QWidget(parent)
{
    setupUi(this);
    updateUI();
}

MainWindow::~MainWindow()
{
}

void MainWindow::closeEvent(QCloseEvent* event)
{
    if (!saveIfNeeded())
        event->ignore();
    else
        event->accept();
}

bool MainWindow::saveIfNeeded()
{
    if (m_Project && m_Project->isModified()) {
        int r = QMessageBox::question(this, tr("Confirmation"), tr("Current file has been modified. Save?"),
            QMessageBox::Save | QMessageBox::Discard | QMessageBox::Cancel);
        if (r == QMessageBox::Save)
            return on_uiSaveFileButton_clicked();
        else
            return r == QMessageBox::Discard;
    }
    return true;
}

void MainWindow::on_uiNewFileButton_clicked()
{
    if (!saveIfNeeded())
        return;

    QString path = QFileDialog::getSaveFileName(this, tr("Create project"), m_FileName, tr("Project file (*.resproj)"));
    if (path.length() == 0)
        return;

    m_Project.reset(new Project);
    connect(m_Project.get(), SIGNAL(updateUI()), SLOT(updateUI()));
    m_FileName = path;

    QString message = tr("Unknown error.");
    if (!m_Project->save(m_FileName, &message)) {
        QMessageBox::critical(this, tr("Error"), tr("Unable to create file \"%1\": %2").arg(m_FileName).arg(message));
        m_Project.reset();
    }

    updateUI();
}

void MainWindow::on_uiOpenFileButton_clicked()
{
    if (!saveIfNeeded())
        return;

    QString path = QFileDialog::getOpenFileName(this, tr("Open project"), m_FileName, tr("Project file (*.resproj)"));
    if (path.length() == 0)
        return;

    m_Project.reset(new Project);
    connect(m_Project.get(), SIGNAL(updateUI()), SLOT(updateUI()));
    m_FileName = path;

    QString message = tr("Unknown error.");
    if (!m_Project->load(m_FileName, &message)) {
        QMessageBox::critical(this, tr("Error"), tr("Unable to load file \"%1\": %2").arg(m_FileName).arg(message));
        m_Project.reset();
    }

    updateUI();
}

bool MainWindow::on_uiSaveFileButton_clicked()
{
    if (!m_Project)
        return true;

    QString message = tr("Unknown error.");
    if (!m_Project->save(m_FileName, &message)) {
        updateUI();
        QMessageBox::critical(this, tr("Error"), tr("Unable to write file \"%1\": %2").arg(m_FileName).arg(message));
        return false;
    }

    updateUI();

    return true;
}

void MainWindow::on_uiAddRuleButton_clicked()
{
    if (!m_Project)
        return;

    NewRuleDialog dialog(this);
    if (dialog.exec() == QDialog::Accepted) {
        BuilderFactory* factory = dialog.selectedFactory();
        if (factory) {
            QMessageBox::critical(this, tr(""), factory->builderName());
        }
    }

    updateUI();
}

void MainWindow::on_uiRemoveRuleButton_clicked()
{
    if (!m_Project)
        return;

    QList<QListWidgetItem*> selectedRules = uiRuleList->selectedItems();
    if (selectedRules.count() == 0)
        return;

    int r = QMessageBox::question(this, tr("Confirmation"),
        tr("Do you wish to remove selected rule from the project?\n\nThis action can't be undone!"),
        QMessageBox::Yes | QMessageBox::No);
    if (r == QMessageBox::No)
        return;

    // FIXME

    updateUI();
}

void MainWindow::on_uiDraftBuildButton_clicked()
{
    if (!m_Project)
        return;

    // FIXME
}

void MainWindow::on_uiFinalBuildButton_clicked()
{
    if (!m_Project)
        return;

    // FIXME
}

void MainWindow::on_uiCleanButton_clicked()
{
    if (!m_Project)
        return;

    // FIXME
}

void MainWindow::on_uiRuleList_itemSelectionChanged()
{
    updateUI();
}

void MainWindow::updateUI()
{
    QList<QListWidgetItem*> selectedRules = uiRuleList->selectedItems();

    uiRuleList->setEnabled(m_Project != nullptr);
    uiSaveFileButton->setEnabled(m_Project != nullptr && m_Project->isModified());
    uiAddRuleButton->setEnabled(m_Project != nullptr);
    uiRemoveRuleButton->setEnabled(m_Project != nullptr && selectedRules.count() > 0);
    uiDraftBuildButton->setEnabled(m_Project != nullptr);
    uiFinalBuildButton->setEnabled(m_Project != nullptr);
    uiCleanButton->setEnabled(m_Project != nullptr);
}